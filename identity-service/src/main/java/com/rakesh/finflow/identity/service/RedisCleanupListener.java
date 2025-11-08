package com.rakesh.finflow.identity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisCleanupListener implements ApplicationListener<ContextClosedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("Application shutting down… cleaning Redis");

        Set<String> accessKeys = redisTemplate.keys("access:*");
        if (accessKeys != null && !accessKeys.isEmpty()) {
            redisTemplate.delete(accessKeys);
        }

        Set<String> refreshKeys = redisTemplate.keys("refresh:*");
        if (refreshKeys != null && !refreshKeys.isEmpty()) {
            redisTemplate.delete(refreshKeys);
        }
    }
}
