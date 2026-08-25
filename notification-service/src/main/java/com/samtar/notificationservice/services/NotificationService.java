package com.samtar.notificationservice.services;


import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.avro.ProductUpdatedEvent;
import com.samtar.consts.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "notification-service-group")
    public void handleProductCreationEvent(ProductCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received product-created event {}", event.getEventId());
        Map<String, String> vars = new HashMap<>();
        vars.put("recipientName", "there");
        vars.put("productId", str(event.getProductId()));
        vars.put("productName", str(event.getProductName()));
        vars.put("description", str(event.getDescription()));
        vars.put("price", formatPrice(event.getPrice()));
        vars.put("quantity", String.valueOf(event.getQuantity()));
        emailService.sendHtml(
                str(event.getEmail()),
                "New product added: " + event.getProductName(),
                "templates/email/product-created.html",
                vars);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_UPDATED, groupId = "notification-service-group")
    public void handleProductUpdateEvent(ProductUpdatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received product-updated event {}", event.getEventId());
        Map<String, String> vars = new HashMap<>();
        vars.put("recipientName", "there");
        vars.put("productId", str(event.getProductId()));
        vars.put("productName", str(event.getProductName()));
        vars.put("description", str(event.getDescription()));
        vars.put("price", formatPrice(event.getPrice()));
        vars.put("quantity", String.valueOf(event.getQuantity()));
        emailService.sendHtml(
                str(event.getEmail()),
                "Product updated: " + event.getProductName(),
                "templates/email/product-updated.html",
                vars);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_DELETED, groupId = "notification-service-group")
    public void handleProductDeletionEvent(ProductDeletedEvent event, Acknowledgment acknowledgment) {
        log.info("Received product-deleted event {}", event.getEventId());
        Map<String, String> vars = new HashMap<>();
        vars.put("recipientName", "there");
        vars.put("productId", str(event.getProductId()));
        vars.put("productName", str(event.getProductName()));
        emailService.sendHtml(
                str(event.getEmail()),
                "Product removed: " + event.getProductName(),
                "templates/email/product-deleted.html",
                vars);
        acknowledgment.acknowledge();
    }

    // Avro string fields are CharSequence (Utf8); normalise to String, null-safe.
    private static String str(CharSequence value) {
        return value == null ? "" : value.toString();
    }

    // price is an Avro decimal (bytes, scale 2) -> render as a plain decimal string.
    private static String formatPrice(ByteBuffer price) {
        if (price == null) {
            return "";
        }
        ByteBuffer copy = price.duplicate();
        byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return new BigDecimal(new BigInteger(bytes), 2).toPlainString();
    }
}
