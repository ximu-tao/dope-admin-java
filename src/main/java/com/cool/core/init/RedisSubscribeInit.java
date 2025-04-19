package com.cool.core.init;

import com.cool.core.annotation.RedisSubscribe;
import com.cool.core.util.RedisUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class RedisSubscribeInit implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        if (beanClass.isAnnotationPresent(RedisSubscribe.class)) {

            RedisSubscribe annotation = beanClass.getAnnotation(RedisSubscribe.class);

            String channel = annotation.channel();
            Class<?> clazz = annotation.clazz();
            if ( bean instanceof Consumer){
                RedisUtils.subscribe( channel , clazz , (Consumer)bean );
            }

        }
        return bean;
    }
}