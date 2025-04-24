package com.cool.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EpsField {

    /**
     * 管理端生成的组件类型
     * @return
     */
    String component() default "input";

    /**
     * 排除 EQ 查询条件
     * @return
     */
    boolean excludeEq() default false;

    /**
     * 在 List、Page 的查询结果中排除
     * @return
     */
    boolean excludeListSelect() default false;

    /**
     * 支持使用 KeyWord 模糊查询
     * @return
     */
    boolean quickQuery() default false;
    
}
