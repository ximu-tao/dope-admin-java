package com.cool.modules.dict.entity;

import com.cool.core.base.BaseEntity;
import org.dromara.autotable.annotation.AutoColumn;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "dict_info", comment = "字典信息")
public class DictInfoEntity extends BaseEntity<DictInfoEntity> {

    @AutoColumn(comment = "类型ID", notNull = true)
    private Long typeId;

    @AutoColumn(comment = "父ID")
    private Long parentId;

    @AutoColumn(comment = "名称", notNull = true)
    private String name;

    @AutoColumn(comment = "值")
    private String value;

    @AutoColumn(comment = "排序", defaultValue = "0")
    private Integer orderNum;

    @AutoColumn(comment = "备注")
    private String remark;

}
