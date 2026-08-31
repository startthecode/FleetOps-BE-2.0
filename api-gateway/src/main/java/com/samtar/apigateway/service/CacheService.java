package com.samtar.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
final RedisTemplate<String,Object> redisTemplate;

    public <T> T get(String key ,Class<T> classN){
    Object value = redisTemplate.opsForValue().get(key);
    if(value == null){
        return null;
    }
    return classN.cast(value);
    }

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true; // cache set successfully
        } catch (Exception e) {
            log.warn("Failed to write cache key {}", key, e);
            return false; // cache set failed
        }
    }

    public boolean delete(String key) {
        try {
            redisTemplate.delete(key);
            return true; // cache delete successfully
        } catch (Exception e) {
            log.warn("Failed to write cache key {}", key, e);
            return false; // cache set failed
        }
    }

    public boolean delete(List<String> key) {
        try {
            redisTemplate.delete(key);
            return true; // cache delete successfully
        } catch (Exception e) {
            log.warn("Failed to write cache key {}", key, e);
            return false; // cache set failed
        }
    }


}
