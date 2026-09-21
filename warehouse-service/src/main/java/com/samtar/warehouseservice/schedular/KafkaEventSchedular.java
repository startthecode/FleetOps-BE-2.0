package com.samtar.warehouseservice.schedular;

import com.samtar.warehouseservice.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventSchedular {
    private final OutboxEventService outboxEventService;

    @Scheduled(fixedDelayString = "10000")
    public void publishEvents(){
        log.info("Event schedular start---");
        outboxEventService.publishEvent();
    }
    @Scheduled(fixedDelayString = "10000")
    public void recoverLockedEvents(){
        log.info("Event schedular recovery---");
        outboxEventService.recoverLockedEvents();
    }
}
