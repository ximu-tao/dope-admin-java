package com.cool.modules.base.entity.sys;

import com.cool.core.base.TenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Ignore;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.enums.IndexTypeEnum;

import java.util.List;

@Getter
@Setter
@Table(value = "base_sys_user", comment = "系统用户表")
public class BaseSysUserEntity extends TenantEntity<BaseSysUserEntity> {
    @Index
    @AutoColumn(comment = "部门ID", type = "bigint")
    private Long departmentId;

    @AutoColumn(comment = "姓名")
    private String name;

    @Index(type = IndexTypeEnum.UNIQUE)
    @AutoColumn(comment = "用户名", length = 100, notNull = true)
    private String username;

    @AutoColumn(comment = "密码", notNull = true)
    private String password;

    @AutoColumn(comment = "密码版本", defaultValue = "1")
    private Integer passwordV;

    @AutoColumn(comment = "昵称", notNull = true)
    private String nickName;

    @AutoColumn(comment = "头像")
    private String headImg;

    @AutoColumn(comment = "手机号")
    private String phone;

    @AutoColumn(comment = "邮箱")
    private String email;

    @AutoColumn(comment = "备注")
    private String remark;

    @AutoColumn(comment = "状态 0:禁用 1：启用", defaultValue = "1")
    private Integer status;

    // 部门名称
    @Column(ignore = true)
    private String departmentName;

    // 角色名称
    @Column(ignore = true)
    private String roleName;

    @AutoColumn(comment = "socketId")
    private String socketId;
    
    
    @Ignore
    @Schema( description = "角色列表" )
    private List<Long> roleIdList;

    
}
