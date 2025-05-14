package com.cool.core.event;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听容器（支持自定义事件名且类型安全）
 */
@Component
public class EventListenerContainer implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;
    private final RedissonClient redissonClient;
    private final Map<String, RTopic> topicMap = new ConcurrentHashMap<>();

    @Autowired
    public EventListenerContainer(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println( "onApplicationEvent" );
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Component.class);
        for (Object bean : beans.values()) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            for (Method method : targetClass.getDeclaredMethods()) {
                System.out.println( method.getName() );
                Subscribe subscribe = AnnotationUtils.findAnnotation(method, Subscribe.class);
                if (subscribe != null) {
                    String eventName = subscribe.value();
                    Class<?> eventType = getExpectedEventType(method, subscribe);
                    subscribeEvent(bean, method, eventName, eventType);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void subscribeEvent(Object bean, Method method, String eventName, Class<T> eventType) {
        RTopic topic =  topicMap.computeIfAbsent(
            eventName, 
            k -> redissonClient.getTopic(eventName)
        );
        
        MessageListener<T> listener = (channel, msg) -> {
            try {
                method.setAccessible(true);
                method.invoke(bean, msg);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke event listener method for event: " + eventName, e);
            }
        };
        
        topic.addListener(eventType, listener);
    }

    private Class<?> getExpectedEventType(Method method, Subscribe subscribe) {
        // 优先使用注解中指定的类型
        if (subscribe.eventType() != Object.class) {
            return subscribe.eventType();
        }
        
        // 从方法参数推断类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("@Subscribe method must have exactly one parameter");
        }
        return parameterTypes[0];
    }
}