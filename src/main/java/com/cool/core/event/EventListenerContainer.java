package com.cool.core.event;

import com.cool.core.base.BaseEntity;
import com.cool.core.lock.CoolLock;
import org.redisson.api.RLock;
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
import java.util.concurrent.TimeUnit;

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

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Component.class);
        for (Object bean : beans.values()) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            for (Method method : targetClass.getDeclaredMethods()) {

                Subscribe subscribe = AnnotationUtils.findAnnotation(method, Subscribe.class);
                if (subscribe != null) {
                    String eventName = subscribe.value();
                    Boolean once = subscribe.once();
                    Class<?> eventType = getExpectedEventType(method, subscribe);
                    subscribeEvent(bean, method, eventName, eventType, once);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void subscribeEvent(Object bean, Method method, String eventName, Class<T> eventType, Boolean once) {
        RTopic topic = topicMap.computeIfAbsent(
                eventName,
                k -> redissonClient.getTopic(eventName)
        );

        MessageListener<T> listener = (channel, msg) -> {
            if (!Boolean.TRUE.equals(once)) {
                // 如果不是once模式，正常调用
                try {
                    method.setAccessible(true);
                    method.invoke(bean, msg);
                } catch (Exception e) {
                    throw new RuntimeException("事件监听方法调用失败: " + eventName, e);
                }
                return;
            }

            // 对于once模式，使用分布式锁确保唯一执行
            String lockKey = "event:once:lock:" + bean.getClass().getName() + method.getName() + eventName + ":" + generateMessageId(msg);

            RLock lock = redissonClient.getLock(lockKey);
            try {
                // 尝试获取锁（不等待，立即返回）
                if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean, msg);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("ERROR: " + eventName, e);
            }
        };

        topic.addListener(eventType, listener);
    }

    // 生成消息唯一标识的方法
    private String generateMessageId(Object msg) {
        // 实际项目中应根据消息内容生成更可靠的ID
        if (msg instanceof BaseEntity<?>) {
            return ((BaseEntity) msg).getId().toString();
        }
        return String.valueOf(msg.hashCode());
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