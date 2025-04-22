package com.cool.core.pay;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Column;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BasePayableEntity<T extends BaseEntity<T>> extends BaseEntity<T> implements PayableEntity, BelongingUserEntity {
    
    @Column(comment = "用户ID")
    @Schema( description = "用户ID（无需提交）")
    @ColumnDefine(comment = "用户ID", notNull = true)
    protected Long userId;
    
    @Column(comment = "支付渠道")
    @ColumnDefine(comment = "支付渠道 wechat-微信支付 alipay-支付宝", notNull = true)
    @NotNull(message = "支付渠道")
    @Schema(  description = "支付渠道:wechat=微信支付,alipay=支付宝")
    protected String payWay;

    
    @Column(comment = "支付流水号")
    @ColumnDefine(comment = "支付流水号" )
    @Schema(hidden = true)
    protected String outTradeNo;
    
    
    @Column(comment = "支付状态" , onInsertValue = "1" )
    @ColumnDefine(comment = "支付状态 1-待支付 2-已支付", notNull = true)
    protected Integer payStatus;
    
    @Column(comment = "客户端类型")
    @ColumnDefine(comment = "客户端类型 mp_wechat-微信小程序 app-手机端 h5-手机网页", notNull = true)
    @NotNull(message = "客户端类型")
    @Schema( description = "客户端类型:mini=微信小程序,app=手机端" )
    protected String terminal;
    
        
    @ColumnDefine(comment = "支付时间" )
    protected LocalDateTime payTime;
    
}
