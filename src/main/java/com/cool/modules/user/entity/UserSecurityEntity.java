package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Table(value = "user_security" , comment = "用户重要数据")
public class UserSecurityEntity extends BaseEntity<UserSecurityEntity> implements BelongingUserEntity {
    
    @Column(comment = "用户ID")
    @Schema( description = "用户ID（无需提交）")
    @ColumnDefine(comment = "用户ID", notNull = true)
    protected Long userId;
    
    @ColumnDefine(comment = "余额")
    @Column(onInsertValue = "0.0")
    private BigDecimal balance;
    
    @ColumnDefine(comment = "积分")
    @Column(onInsertValue = "0")
    private Integer points;
    
    @Column(version = true , onInsertValue = "0")
    private Integer version;

}
