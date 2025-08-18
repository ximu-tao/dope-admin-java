package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "base_sys_param", comment = "系统参数配置")
public class BaseSysParamEntity extends BaseEntity<BaseSysParamEntity> {
    @UniIndex
    @AutoColumn(comment = "键", notNull = true)
    private String keyName;

    @AutoColumn(comment = "名称")
    private String name;

    @AutoColumn(comment = "数据", type = "text")
    private String data;

    @AutoColumn(comment = "数据类型 0:字符串 1:数组 2:键值对", defaultValue = "0")
    private Integer dataType;

    @AutoColumn(comment = "备注")
    private String remark;
    
    @AutoColumn(comment = "允许公开的参数")
    private Boolean open;
}
