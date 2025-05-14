package com.cool.core.event;

/**
 * 事件发布接口
 */
public interface EventPublisher {
    /**
     * 发布事件
     * @param eventName 事件名称
     * @param event 事件对象
     */
    void publish(String eventName, Object event);
}