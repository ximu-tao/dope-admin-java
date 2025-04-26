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
     * 支持 like 查询
     */
    boolean like() default false;
    
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
    



    /**
     * 不可修改的字段（仅APP端接口有效）（注意： Update 或 Delete 时，如果该字段不为 null 会被作为 UPDATE 或 DELETE 条件）
     * @return
     */
    boolean immutable() default false;
}
