package com.cool.modules.space.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import lombok.Getter;
import lombok.Setter;

/**
 * 图片空间信息分类
 */
@Getter
@Setter
@Table(value = "space_type", comment = "图片空间信息分类")
public class SpaceTypeEntity extends BaseEntity<SpaceTypeEntity> {
    @AutoColumn(comment = "类别名称", notNull = true)
    private String name;

    @AutoColumn(comment = "父分类ID")
    private Integer parentId;
}
