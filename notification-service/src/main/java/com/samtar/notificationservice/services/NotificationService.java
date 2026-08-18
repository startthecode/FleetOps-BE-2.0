package com.samtar.notificationservice.services;


import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.avro.ProductUpdatedEvent;
import com.samtar.consts.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "notification-service-group")
    public void handleProductCreationEvent(ProductCreatedEvent productCreatedEvent, Acknowledgment acknowledgment) {
        log.info("Event recieved {}", productCreatedEvent);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_UPDATED, groupId = "notification-service-group")
    public void handleProductCreationEvent(ProductUpdatedEvent productUpdatedEvent, Acknowledgment acknowledgment) {
        log.info("Event recieved updated {}", productUpdatedEvent);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_DELETED, groupId = "notification-service-group")
    public void handleProductCreationEvent(ProductDeletedEvent productDeleteEvent, Acknowledgment acknowledgment) {
        log.info("Event recieved {}", productDeleteEvent);
        acknowledgment.acknowledge();
    }

}
