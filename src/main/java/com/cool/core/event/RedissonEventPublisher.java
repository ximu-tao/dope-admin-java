package com.cool.core.event;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Redisson事件发布器实现（支持自定义事件名）
 */
@Component
public class RedissonEventPublisher implements EventPublisher {

    private final RedissonClient redissonClient;

    @Autowired
    public RedissonEventPublisher(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void publish(String eventName, Object event) {
        RTopic topic = redissonClient.getTopic(eventName);
        topic.publish(event);
    }
}