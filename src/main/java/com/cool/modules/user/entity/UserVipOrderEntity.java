package com.cool.modules.user.entity;

import com.cool.core.annotation.EspRemoteSelectField;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.pay.PayableEntity;
import com.cool.core.pay.BasePayableEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema( description = "会员开通订单")
@Table(value = "user_vip_order", comment = "会员订单")
public class UserVipOrderEntity extends BasePayableEntity<UserVipOrderEntity> implements PayableEntity<UserVipOrderEntity>, BelongingUserEntity {
    
    @ColumnDefine(comment = "价格", notNull = true)
    @Schema(hidden = true)
    protected BigDecimal price;
    
    @ColumnDefine(comment = "商品标题", notNull = true)
    @Schema(hidden = true)
    protected String title;
    
    
    @Column(comment = "开通的会员ID")
    @ColumnDefine(comment = "开通的会员ID", notNull = true)
    @NotNull(message = "开通的会员ID")
    @Schema( description = "开通的会员ID" )
    @EspRemoteSelectField( clazz = UserVipInfoEntity.class )
    private Long vipId;
    
    
    @Column(comment = "开通时长（天）")
    @ColumnDefine(comment = "开通时长（天）" )
    @Schema(hidden = true)
    protected Integer day;
    

    @Override
    public String getBody() {
        return title;
    }

    @Override
    public Double getTotal() {
        return price.doubleValue();
    }

    @Override
    public BigDecimal getTotalAmount() {
        return price;
    }
}
