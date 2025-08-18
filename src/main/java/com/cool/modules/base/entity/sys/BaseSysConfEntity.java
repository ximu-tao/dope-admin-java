package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.enums.IndexTypeEnum;

/**
 * 系统配置
 */
@Getter
@Setter
@Table(value = "base_sys_conf", comment = "系统配置表")
public class BaseSysConfEntity extends BaseEntity<BaseSysConfEntity> {
    @Index(type = IndexTypeEnum.UNIQUE)
    @AutoColumn(comment = "配置键", notNull = true)
    private String cKey;

    @AutoColumn(comment = "值", notNull = true, type = "text")
    private String cValue;
}
