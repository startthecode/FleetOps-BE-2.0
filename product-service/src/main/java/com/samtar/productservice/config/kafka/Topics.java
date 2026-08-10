package com.samtar.productservice.config.kafka;

import com.samtar.enums.kafkaEvents.ProductEvents;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topics {
    @Bean
    public NewTopic productCreated(){
        return TopicBuilder
                .name(ProductEvents.CREATED.toString())
                .partitions(8)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productUpdated(){
        return TopicBuilder
                .name(ProductEvents.UPDATED.toString())
                .partitions(8)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productDeleted(){
        return TopicBuilder
                .name(ProductEvents.DELETED.toString())
                .partitions(4)
                .replicas(1)
                .build();
    }

}
