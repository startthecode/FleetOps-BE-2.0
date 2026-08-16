package com.samtar.productservice.scheduler;

import com.samtar.productservice.service.ProductService;
import com.samtar.productservice.service.outbox.OutBoxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaEventScheduler {
      private final OutBoxPublisher outBoxPublisher;
      @Scheduled(fixedDelayString = "10000")
//      @Scheduled(fixedDelayString = "${spring.kafka.producer.properties.outbox.interval.ms}")
      public void eventPublisher(){
            log.info("Event schedular start---");
            outBoxPublisher.publishEvents();
      }
      @Scheduled(fixedDelay = 60_000)
      public void recoverEvents(){
            log.info("Event schedular recovery---");
            outBoxPublisher.recoverStuckEvents();

      }
}
