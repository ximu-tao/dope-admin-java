package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;

import org.dromara.autotable.annotation.AutoColumn;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统部门
 */
@Getter
@Setter
@Table(value = "base_sys_department", comment = "系统部门")
public class BaseSysDepartmentEntity extends BaseEntity<BaseSysDepartmentEntity> {
    @AutoColumn(comment = "部门名称", notNull = true)
    private String name;

    @AutoColumn(comment = "上级部门ID", type = "bigint")
    private Long parentId;

    @AutoColumn(comment = "排序", defaultValue = "0")
    private Integer orderNum;

    // 父菜单名称
    @Column(ignore = true)
    private String parentName;
}
