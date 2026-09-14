package com.samtar.warehouseservice.config.kafka;


import org.apache.commons.compress.compressors.CompressorException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.concurrent.TimeoutException;

@Configuration
public class ConsumerConfig {
    @Value("${spring.kafka.retry.interval}")
    Long retryInterval;
    @Value("${spring.kafka.retry.multiplier}")
    Double retryMultiplier;
    @Value("${spring.kafka.retry.max-duration}")
    Long retryMaxDuration;

    @Bean
    public DefaultErrorHandler handleError(KafkaTemplate<String, Object> template) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(template, (rec, err) ->
                        new TopicPartition(rec.topic() + "-dlt", -1));
        ExponentialBackOff retry = new ExponentialBackOff(retryInterval, retryMultiplier
        );
        retry.setMaxElapsedTime(retryMaxDuration);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, retry);
        defaultErrorHandler.addNotRetryableExceptions(IllegalArgumentException.class, MethodNotAllowedException.class);
        defaultErrorHandler.addRetryableExceptions(TimeoutException.class, CompressorException.class);
        return defaultErrorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> consumer(
            ConsumerFactory<Object, Object> consumerFactory, DefaultErrorHandler defaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConcurrency(8);
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(defaultErrorHandler);
        return concurrentKafkaListenerContainerFactory;
    }
}
