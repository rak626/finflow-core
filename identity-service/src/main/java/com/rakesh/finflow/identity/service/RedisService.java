package com.rakesh.finflow.identity.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    // update the ttl only
    public void updateTTL(String key, long ttl, TimeUnit unit) {
        redisTemplate.expire(key, ttl, unit);
    }

//    @PreDestroy
//    public void onShutdown() {
//        System.out.println("Spring Boot is shutting down… deleting all Redis keys");
//        Set<String> keys = redisTemplate.keys("access:*");
//        if (keys != null && !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//        }
//        Set<String> keysed = redisTemplate.keys("refresh:*");
//        if (keysed != null && !keysed.isEmpty()) {
//            redisTemplate.delete(keysed);
//        }
//
//    }
}
