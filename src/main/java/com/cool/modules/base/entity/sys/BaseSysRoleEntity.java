package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.cool.core.mybatis.handler.Fastjson2TypeHandler;
import org.dromara.autotable.annotation.AutoColumn;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.enums.IndexTypeEnum;

@Getter
@Setter
@Table(value = "base_sys_role", comment = "系统角色表")
public class BaseSysRoleEntity extends BaseEntity<BaseSysRoleEntity> {

    @Index
    @AutoColumn(comment = "用户ID", notNull = true, type = "bigint")
    private Long userId;

    @AutoColumn(comment = "名称", notNull = true)
    private String name;

    @Index(type = IndexTypeEnum.UNIQUE)
    @AutoColumn(comment = "角色标签", notNull = true)
    private String label;

    @AutoColumn(comment = "备注")
    private String remark;

    @AutoColumn(comment = "数据权限是否关联上下级", defaultValue = "1")
    private Integer relevance;

    @AutoColumn(comment = "菜单权限", type = "json")
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private List<Long> menuIdList;

    @AutoColumn(comment = "部门权限", type = "json")
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private List<Long> departmentIdList;
}
