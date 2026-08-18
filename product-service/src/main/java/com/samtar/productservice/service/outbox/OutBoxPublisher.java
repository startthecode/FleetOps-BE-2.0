package com.samtar.productservice.service.outbox;

import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.avro.ProductUpdatedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.enums.OutboxStatus;
import com.samtar.productservice.entity.OutboxEventEntity;
import com.samtar.productservice.repository.OutBoxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutBoxPublisher {
    public final OutBoxEventRepository outBoxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Maps each topic to the Avro schema used to rebuild its SpecificRecord from the outbox payload.
    private static final Map<String, Schema> TOPIC_SCHEMAS = Map.of(
            KafkaTopics.PRODUCT_CREATED, ProductCreatedEvent.getClassSchema(),
            KafkaTopics.PRODUCT_UPDATED, ProductUpdatedEvent.getClassSchema(),
            KafkaTopics.PRODUCT_DELETED, ProductDeletedEvent.getClassSchema()
    );

    @Transactional
    private List<OutboxEventEntity> claimedEvents() {
        //    @Value("${spring.kafka.producer.properties.outbox.batch.size}")
        int batchSize = 100;
        Pageable pageable = PageRequest.of(0, batchSize);
        List<OutboxEventEntity> eventList = outBoxEventRepository.findByStatus(OutboxStatus.PENDING, pageable);
        eventList.forEach(e -> {
            e.setStatus(OutboxStatus.PROCESSING);
            e.setLockedAt(Instant.now());
        });
        return eventList;
    }

    public void publishEvents() {
        List<OutboxEventEntity> eventBatch = claimedEvents();
        if (eventBatch.isEmpty()) {

            return;
        }
        eventBatch.forEach(e -> {
            kafkaTemplate.send(
                    e.getTopic(),
                    e.getId().toString(),
                    decodeAvro(e.getTopic(), e.getPayload())).whenComplete((d, ex) -> {
                if (ex == null) {
                    markPublished(e);
                } else {
                    log.info("Error in event publish", ex);
                    markFailed(e);
                }
            });
        });


    }

    // Rebuild the exact Avro SpecificRecord from the Base64 binary stored in the outbox,
    // so KafkaAvroSerializer registers/uses the real event schema (not an Avro "string").
    private static SpecificRecordBase decodeAvro(String topic, String base64Payload) {
        Schema schema = TOPIC_SCHEMAS.get(topic);
        if (schema == null) {
            throw new IllegalArgumentException("No Avro schema mapped for topic: " + topic);
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(base64Payload);
            SpecificDatumReader<SpecificRecordBase> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
            return reader.read(null, decoder);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to decode Avro payload for topic: " + topic, ex);
        }
    }

    @Transactional
    private void markPublished(OutboxEventEntity event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(Instant.now());
        event.setLockedAt(null);
        outBoxEventRepository.save(event);
    }

    @Transactional
    private void markFailed(OutboxEventEntity event) {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

        if (event.getCreatedAt().isBefore(oneHourAgo)) {
            // event is older than 1 hour
            event.setStatus(OutboxStatus.FAILED);
        } else {
            event.setStatus(OutboxStatus.PENDING);
        }
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLockedAt(null);
        outBoxEventRepository.save(event);
    }

    public void recoverStuckEvents() {
        Instant timeout = Instant.now().minus(10, ChronoUnit.MINUTES);
        Set<OutboxEventEntity> stuck =
                outBoxEventRepository.findByStatusAndLockedAtBefore(
                        OutboxStatus.PROCESSING,
                        timeout
                );
        if (stuck.isEmpty()) {
            return;
        }
        stuck.forEach(event -> {
            event.setStatus(OutboxStatus.PENDING);
            event.setLockedAt(null);
        });
    }
}
