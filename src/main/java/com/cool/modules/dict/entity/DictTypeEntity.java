package com.cool.modules.dict.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "dict_type", comment = "字典类型")
public class DictTypeEntity extends BaseEntity<DictTypeEntity> {

    @AutoColumn(comment = "名称", notNull = true)
    private String name;

    @AutoColumn(comment = "标识", notNull = true)
    @UniIndex
    private String key;
}
