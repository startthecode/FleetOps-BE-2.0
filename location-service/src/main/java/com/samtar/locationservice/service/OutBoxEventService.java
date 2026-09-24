package com.samtar.locationservice.service;


import com.samtar.avro.CityCreatedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.enums.OutboxStatus;
import com.samtar.enums.Status;
import com.samtar.locationservice.entity.CityEntity;
import com.samtar.locationservice.entity.OutboxEventEntity;
import com.samtar.locationservice.repository.OutboxEventRepository;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OutBoxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> template;
    private static final Map<String, Schema> TOPIC_SCHEMAS = Map.of(
            KafkaTopics.CITY_CREATED, CityCreatedEvent.getClassSchema(),
            KafkaTopics.CITY_DELETED, CityCreatedEvent.getClassSchema()
    );


    private List<OutboxEventEntity> claimEvents() {
        int batchSize = 100;
        Pageable pageable = PageRequest.of(0, batchSize);
        List<OutboxEventEntity> eventList = outboxEventRepository.findByStatus(OutboxStatus.PENDING, pageable);
        Instant time = Instant.now();
        eventList.forEach(e -> {
            e.setStatus(OutboxStatus.PROCESSING);
            e.setLockedAt(time);
        });
        return eventList;
    }

    public void publishEvent() {
        List<OutboxEventEntity> eventList = claimEvents();
        eventList.forEach(e -> {
            template.send(e.getTopic(), e.getId().toString(), decodeAvro(e.getPayload(), e.getPayload()))
                    .whenComplete((success, err) -> {
                        if (err != null) {
                            updateStatus(e, OutboxStatus.FAILED);
                        } else {
                            updateStatus(e, OutboxStatus.PUBLISHED);
                        }
                    });
        });
    }

    private void updateStatus(OutboxEventEntity outboxEvent, OutboxStatus status) {
        outboxEvent.setStatus(status);
        if (outboxEvent.getRetryCount() < 5) {
            outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
            outboxEvent.setStatus(OutboxStatus.PENDING);
        } else {
            outboxEvent.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(outboxEvent);
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

}
