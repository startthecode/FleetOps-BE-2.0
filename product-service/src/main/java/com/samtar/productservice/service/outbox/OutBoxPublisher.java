package com.samtar.productservice.service.outbox;

import com.samtar.enums.OutboxStatus;
import com.samtar.enums.kafkaEvents.ProductEvents;
import com.samtar.productservice.entity.OutboxEventEntity;
import com.samtar.productservice.repository.OutBoxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutBoxPublisher {
    public final OutBoxEventRepository outBoxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
                    e.getPayload()).whenComplete((d, ex) -> {
                if (ex == null) {
                    markPublished(e);
                } else {
                    log.info("Error in event publish", ex);
                    markFailed(e);
                }
            });
        });


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
