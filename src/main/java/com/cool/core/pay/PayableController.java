package com.cool.core.pay;

import com.alipay.api.AlipayApiException;
import com.cool.core.annotation.NoRepeatSubmit;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.base.AppController;
import com.cool.core.base.BaseEntity;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.R;
import com.cool.core.util.ConvertUtil;
import com.cool.core.util.CoolSecurityUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 通用支付控制器
 *
 * @param <S>
 * @param <T>
 */
public abstract class PayableController<S extends PayableService<T>, T extends BaseEntity<T> & PayableEntity<T> > extends AppController<S, T> {

    private static final Logger log = LoggerFactory.getLogger(PayableController.class);

    @Autowired
    private ApplicationContext context;

        
    @Operation(summary = "创建订单", description = "创建订单后使用返回ID值调用同目录 pay 接口")
    @PostMapping("/create")
    public R<T> create(@Valid @RequestBody T requestParams) {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        requestParams.setUserId(userId);

        Long add = this.getService().add(requestParams);
        return R.ok(requestParams);
    }

    
        
    @Operation(summary = "关闭订单", description = "关闭订单，默认仅支持ID参数")
    @PostMapping("/close")
    public R<Boolean> close( @RequestBody T entity){
        T info = this.getService().info( entity.getId() , null );
        CoolPreconditions.checkEmpty(info , "找不到订单");
        
        if ( PayStatusEnum.PAYED.equals( info.getPayStatus() ) ){
            return R.error("订单已支付" );
        }
        
        info.setPayStatus( PayStatusEnum.CANCEL );

        boolean update = this.getService().update(info);
        if (update){
            try {
                this.getService().close( info );
            }catch (Exception e){}
        }

        return R.ok(update);
    }
    
    

    @Data
    @Schema( description = "支付参数" )
    public static class PayInfo{
        
        @Schema( description = "使用 add 接口创建的订单ID，必填")
        @NotNull
        private Long id; 
        
        @Schema( description = "客户端类型 可选值 app、pc、h5、mp_wechat（微信小程序）、oa_wechat（微信公众号）")
        private String terminal;
        
        
        @Schema( description = "支付渠道 可选值 alipay、wechat")
        private String payWay;
        
    }

    @Operation(summary = "支付", description = "支付")
    @PostMapping("/pay")
    @NoRepeatSubmit
    public R pay( @Valid @RequestBody PayInfo pay ) throws Exception {

        String className = this.getClass().getSimpleName();
        String classPath = ConvertUtil.extractController2Path("app", className);

        log.info("调用支付，ID: {}", pay.getId() );

        T info = this.getService().info( pay.getId(), null );
        
        CoolPreconditions.checkEmpty(info , "找不到订单");
        
        if ( PayStatusEnum.PAYED.equals( info.getPayStatus() ) ){
            return R.error("订单已支付");
        }
        
        
        if ( PayStatusEnum.CANCEL.equals( info.getPayStatus() ) ){
            return R.error("订单已关闭");
        }

        
        info.setPayStatus( PayStatusEnum.PAYING );

        
        if ( !StringUtils.isBlank(pay.getTerminal()) ) {
            info.setTerminal( pay.getTerminal() );
        }
        
        if ( !StringUtils.isBlank(pay.getPayWay()) ) {
            info.setPayWay( pay.getPayWay() );
        }
        
        PayWayService payWayService = context.getBean(info.getPayWay(), PayWayService.class);


        service.update( info);


        String payWay = info.getPayWay();

        if (!service.isSupportPayWay(payWay)) {
            return R.error(400, "不支持的支付方式");
        }
        
        String payTerminal = info.getTerminal();
        if ( !service.isSupportTerminal(payTerminal)){
            return R.error(400, "不支持的客户端类型");
        }

        String notifyUrl = getDoamin() + "/app/" + classPath + "/notify/" + payWay;


        Object o = payWayService.create(info, CoolSecurityUtil.getCurrentUserId(), notifyUrl, "", this.getService());
        return R.ok(o);
    }
    
    @Operation(summary = "支付回调通知处理")
    @PostMapping("/notify/{source}")
    @TokenIgnore
    public Object notify(HttpServletRequest request , HttpServletResponse response , @PathVariable("source") String source ) throws Exception {
        
        PayWayService payWayService = context.getBean( source , PayWayService.class);
        
        return payWayService.notify( request , response , this.getService() );
        
    }



}
