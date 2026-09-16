package com.samtar.productservice.service.reference;


import com.samtar.avro.WarehouseCreatedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.enums.kafkaEvents.WareHouseEvents;
import com.samtar.productservice.entity.ProcessedEventsEntity;
import com.samtar.productservice.entity.referenceEntity.WarehouseEntity;
import com.samtar.productservice.repository.ProcessedEventsRepository;
import com.samtar.productservice.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.internals.Acknowledgements;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServices {
    private final WarehouseRepository warehouseRepository;
    private final ProcessedEventsRepository processedEventsRepository;

    @Transactional
    public void create(WarehouseCreatedEvent event) {
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setWarehouseId(UUID.fromString(event.getWarehouseId()));
        warehouseRepository.save(
                warehouseEntity
        );
        ProcessedEventsEntity processedEventsEntity = new ProcessedEventsEntity();
        processedEventsEntity.setEventType(WareHouseEvents.WAREHOUSE_CREATED.toString());
        processedEventsEntity.setEventId(UUID.fromString(event.getEventId()));
        processedEventsEntity.setProcessedAt(Instant.now());
        processedEventsRepository.save(processedEventsEntity);
    }


    @Transactional
    @KafkaListener(topics = KafkaTopics.WAREHOUSE_CREATED, groupId = "product_service")
    public void warehouseCreateEvent(WarehouseCreatedEvent event, Acknowledgment acknowledgment) {
        create(event);
        acknowledgment.acknowledge();
    }


}
