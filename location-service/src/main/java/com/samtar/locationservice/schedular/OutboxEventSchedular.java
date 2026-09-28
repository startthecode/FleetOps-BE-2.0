package com.samtar.locationservice.schedular;

import com.samtar.locationservice.service.OutBoxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventSchedular {
   private final OutBoxEventService eventService;
    @Scheduled(fixedDelayString = "10000")
    public void publishEvent(){
        log.info("Event schedular start---");
        eventService.publishEvent();
    }

    @Scheduled(fixedDelayString = "10000")
    public void recoverDeadEvents(){
        log.info("Event recovery start---");
        eventService.recoverLockedEvents();
    }


}
