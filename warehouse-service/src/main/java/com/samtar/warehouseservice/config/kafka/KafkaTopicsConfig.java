package com.samtar.warehouseservice.config.kafka;


import com.samtar.enums.kafkaEvents.WareHouseEvents;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic warehouseCreated() {
        return TopicBuilder.name(WareHouseEvents.WAREHOUSE_CREATED.toString()).partitions(8).replicas(1).build();
    }

    @Bean
    public NewTopic warehouseUpdated() {
        return TopicBuilder.name(WareHouseEvents.WAREHOUSE_UPDATED.toString()).partitions(8).replicas(1).build();
    }

    @Bean
    public NewTopic warehouseDeleted() {
        return TopicBuilder.name(WareHouseEvents.WAREHOUSE_DELETED.toString()).partitions(8).replicas(1).build();
    }

}
