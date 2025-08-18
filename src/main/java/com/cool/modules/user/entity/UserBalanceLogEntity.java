package com.cool.modules.user.entity;

import com.cool.core.base.AppEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Table(value = "user_balance_log", comment = "用户余额变更记录")
@Data
@Schema( description = "用户余额变更记录")
public class UserBalanceLogEntity extends AppEntity<UserBalanceLogEntity> implements BelongingUserEntity {
    
    
    @AutoColumn(comment = "变更前金额", notNull = true)
    @Schema(description = "变更前金额")
    private BigDecimal oldBalance;
    
    @AutoColumn(comment = "变更金额", notNull = true)
    @Schema(description = "变更金额")
    private BigDecimal balance;
    
    @AutoColumn(comment = "变更后金额", notNull = true)
    @Schema(description = "变更后金额")
    private BigDecimal newBalance;
    
    @AutoColumn(comment = "备注", notNull = true)
    @Schema(description = "备注")
    private String remarks;
}
