package com.samtar.locationservice.config.kafka;


import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducer {

    @Bean
    public ProducerFactory<String,Object> kafkaProducerFactory(KafkaProperties kafkaProperties){
        return new DefaultKafkaProducerFactory<String,Object>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String,Object> kafkaTemplate(ProducerFactory<String,Object> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}
