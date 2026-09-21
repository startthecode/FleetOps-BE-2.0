package com.samtar.warehouseservice.service;


import com.samtar.avro.*;
import com.samtar.consts.KafkaTopics;
import com.samtar.enums.OutboxStatus;
import com.samtar.warehouseservice.entity.OutboxEventEntity;
import com.samtar.warehouseservice.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> template;
    // Maps each topic to the Avro schema used to rebuild its SpecificRecord from the outbox payload.
    private static final Map<String, Schema> TOPIC_SCHEMAS = Map.of(
            KafkaTopics.WAREHOUSE_CREATED, WarehouseCreatedEvent.getClassSchema(),
            KafkaTopics.WAREHOUSE_DELETED, WarehouseDeletedEvent.getClassSchema()
    );


    @Transactional
    public List<OutboxEventEntity> claimEvents() {
        int batchSize = 100;
        Pageable pageable = PageRequest.of(0, batchSize);
        List<OutboxEventEntity> eventList = outboxEventRepository.findByStatus(OutboxStatus.PENDING, pageable);
        Instant now = Instant.now();
        eventList.forEach(e -> {
            e.setStatus(OutboxStatus.PROCESSING);
            e.setLockedAt(now);
        });
        return outboxEventRepository.saveAll(eventList);
    }

    public void publishEvent() {
        List<OutboxEventEntity> eventList = claimEvents();
        eventList.forEach(e -> {
            template.send(e.getTopic(), e.getId().toString(), decodeAvro(e.getTopic(), e.getPayload())).whenComplete((pass, fail) -> {
                if (fail == null) {
                    updateStatus(e, OutboxStatus.FAILED);
                } else {
                    updateStatus(e, OutboxStatus.PUBLISHED);
                }
            });
        });

    }

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
    public void updateStatus(OutboxEventEntity entity, OutboxStatus status) {
        entity.setStatus(status);
        outboxEventRepository.save(entity);
    }


    @Transactional
    public void recoverLockedEvents() {
        Instant timeout = Instant.now().minus(10, ChronoUnit.MINUTES);
        Set<OutboxEventEntity> stuck =
                outboxEventRepository.findByStatusAndLockedAtBefore(
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
        outboxEventRepository.saveAll(stuck);
    }

}
