package com.cool.core.event;

import java.lang.annotation.*;

/**
 * 事件订阅注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
    /**
     * 订阅的事件名称
     */
    String value();
    
    /**
     * 事件参数类型（可选，用于方法参数类型检查）
     */
    Class<?> eventType() default Object.class;


    /**
     * 分布式系统下，本方法只调用一次
     * @return
     */
    boolean once() default false;
}