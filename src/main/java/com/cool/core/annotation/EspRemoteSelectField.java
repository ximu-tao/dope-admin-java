package com.cool.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EspRemoteSelectField {

    /**
     * 目标表 entity
     * @return
     */
    Class<?> clazz() default String.class;

    /**
     * 选择时显示的字段
     * @return
     */
    String titleField() default "title";

    /**
     * 是否允许多选
     * @return
     */
    boolean multiple() default false;
}
