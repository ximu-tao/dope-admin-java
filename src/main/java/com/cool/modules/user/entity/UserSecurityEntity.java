package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(value = "user_security" , comment = "用户重要数据")
public class UserSecurityEntity extends BaseEntity<UserSecurityEntity> implements BelongingUserEntity {
    
    @Column(comment = "用户ID")
    @Schema( description = "用户ID（无需提交）")
    @AutoColumn(comment = "用户ID", notNull = true)
    protected Long userId;
    
    @AutoColumn(comment = "余额")
    @Column(onInsertValue = "0.0")
    private BigDecimal balance;
    
    @AutoColumn(comment = "积分")
    @Column(onInsertValue = "0")
    private Integer points;
    
        
    @Column(onInsertValue = "now()" )
    @AutoColumn( comment = "会员过期时间" )
    private Date vipTime;
    
    @Column(version = true , onInsertValue = "0")
    private Integer version;

}
