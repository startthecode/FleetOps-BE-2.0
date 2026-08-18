package com.samtar.notificationservice.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.server.MethodNotAllowedException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConsumer {

    @Value("${notification.kafka.retry.interval}")
    Long retryInterval;

    @Value("${notification.kafka.retry.multiplier}")
    Double retryMultiplier;

    @Value("${notification.kafka.retry.max-duration}")
    Long retryMaxDuration;

    @Bean
    public DefaultErrorHandler handleError(KafkaTemplate<String,Object> template){
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(template,
                        (record, ex) -> new TopicPartition(record.topic() + "-dlt", -1));
        ExponentialBackOff retry = new
                ExponentialBackOff(retryInterval,retryMultiplier);
        retry.setMaxElapsedTime(retryMaxDuration);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer,retry
        );
        log.info("retry backoff -> interval={}ms multiplier={} maxDuration={}ms", retryInterval, retryMultiplier, retryMaxDuration);
        defaultErrorHandler.addRetryableExceptions(TimeoutException.class, ConnectException.class);
        defaultErrorHandler.addNotRetryableExceptions(IllegalArgumentException.class, MethodNotAllowedException.class);
        return  defaultErrorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object,Object> consumerFactory(ConsumerFactory<Object,Object> consumerFactory,DefaultErrorHandler defaultErrorHandler){
        ConcurrentKafkaListenerContainerFactory<Object,Object> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
            concurrentKafkaListenerContainerFactory.setConcurrency(8);
            concurrentKafkaListenerContainerFactory.setCommonErrorHandler(defaultErrorHandler);
            return  concurrentKafkaListenerContainerFactory;
    }



}
