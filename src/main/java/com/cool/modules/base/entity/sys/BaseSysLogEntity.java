package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.cool.core.mybatis.handler.Fastjson2TypeHandler;
import org.dromara.autotable.annotation.AutoColumn;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "base_sys_log", comment = "系统日志表")
public class BaseSysLogEntity extends BaseEntity<BaseSysLogEntity> {

    @Index
    @AutoColumn(comment = "用户ID", type = "bigint")
    private Long userId;

    @AutoColumn(comment = "行为", length = 1000)
    private String action;

    @AutoColumn(comment = "IP", length = 50)
    private String ip;

    @AutoColumn(comment = "参数", type = "json")
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Object params;

    // 用户名称
    @Column(ignore = true)
    private String name;
}
