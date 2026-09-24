package com.samtar.locationservice.config.kafka;

import com.samtar.consts.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topics {
    @Bean
    public NewTopic cityCreated(){
        return TopicBuilder.name(KafkaTopics.CITY_CREATED).partitions(8).replicas(2).build();
    }
    @Bean
    public NewTopic cityDeleted(){
        return TopicBuilder.name(KafkaTopics.CITY_CREATED).partitions(8).replicas(2).build();
    }
}
