package com.cool.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RedisSubscribe {
    /**
     * 监听的频道名称
     */
    String channel();
    
    Class<?> clazz() default String.class;
}
