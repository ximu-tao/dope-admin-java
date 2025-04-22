package com.cool.modules.user.entity;

import com.cool.core.annotation.EspRemoteSelectField;
import com.cool.core.base.AppEntity;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.base.PayableEntity;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema( description = "会员开通订单")
@Table(value = "user_vip_order", comment = "会员订单")
public class UserVipOrderEntity extends AppEntity<UserVipOrderEntity> implements PayableEntity, BelongingUserEntity {
    
    @ColumnDefine(comment = "价格", notNull = true)
    @Schema(hidden = true)
    protected Double price;
    
    @ColumnDefine(comment = "商品标题", notNull = true)
    @Schema(hidden = true)
    protected String title;
    
    @Column(comment = "支付渠道")
    @ColumnDefine(comment = "支付渠道 wechat-微信支付 alipay-支付宝", notNull = true)
    @NotNull(message = "支付渠道")
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED , description = "支付渠道:wechat=微信支付,alipay=支付宝")
    protected String payWay;

    
    @Column(comment = "支付流水号")
    @ColumnDefine(comment = "支付流水号" )
    @Schema(hidden = true)
    protected String outTradeNo;
    
    
    @Column(comment = "支付状态" , onInsertValue = "1" )
    @ColumnDefine(comment = "支付状态 1-待支付 2-已支付", notNull = true)
    protected Integer payStatus;
    
    
    @Column(comment = "开通的会员ID")
    @ColumnDefine(comment = "开通的会员ID", notNull = true)
    @NotNull(message = "开通的会员ID")
    @Schema( description = "开通的会员ID" )
    @EspRemoteSelectField( clazz = UserVipInfoEntity.class )
    private Long vipId;
    
    @Column(comment = "客户端类型")
    @ColumnDefine(comment = "客户端类型 mp_wechat-微信小程序 app-手机端 h5-手机网页", notNull = true)
    @NotNull(message = "客户端类型")
    @Schema( description = "客户端类型:mini=微信小程序,app=手机端" )
    protected String terminal;

    
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
        return price;
    }
}
