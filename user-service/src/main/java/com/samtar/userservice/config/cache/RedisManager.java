package com.samtar.userservice.config.cache;

import com.samtar.consts.CacheKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisManager {
    final int sessionExpiry;
    public RedisManager(@Value("${app.security.cache.session-expiry}") int sessionExpiry) {
        this.sessionExpiry = sessionExpiry;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory){
        RedisCacheConfiguration defaultConfig =  RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues();

        Map<String,RedisCacheConfiguration> initialCacheConfig = new HashMap<>();
        initialCacheConfig.put(
                CacheKeys
                        .USER_AUTH_SESSION.toString(),
                RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(sessionExpiry)));

        return RedisCacheManager
                .builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(initialCacheConfig)
                .build();
    }
}
