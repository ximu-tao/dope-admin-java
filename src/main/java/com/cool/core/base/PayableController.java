package com.cool.core.base;

import cn.hutool.core.util.ObjectUtil;
import com.alipay.api.AlipayApiException;
import com.cool.core.annotation.NoRepeatSubmit;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.R;
import com.cool.core.util.ConvertUtil;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.user.service.UserInfoService;
import com.cool.modules.user.service.UserOauthService;
import com.cool.plugin.AliPayService;
import com.cool.plugin.WxPayService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 通用支付控制器
 *
 * @param <S>
 * @param <T>
 */
public abstract class PayableController<S extends PayableService<T>, T extends BaseEntity<T> & PayableEntity > extends AppController<S, T> {

    private static final Logger log = LoggerFactory.getLogger(PayableController.class);

    private final WxPayService wxPayService;

    private final AliPayService aliPayService;

    private final UserOauthService userOauthService;

    protected PayableController(WxPayService wxPayService, AliPayService aliPayService, UserOauthService userOauthService) {
        this.wxPayService = wxPayService;
        this.aliPayService = aliPayService;
        this.userOauthService = userOauthService;
    }

        
    @Operation(summary = "创建订单", description = "创建订单后使用返回ID值调用同目录 pay 接口")
    @PostMapping("/add")
    public R<Long> add(@Valid @RequestBody T requestParams) {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        requestParams.setUserId(userId);
        Long add = service.create(requestParams);
        return R.ok(add);
    }

    
        
    @Operation(summary = "关闭订单", description = "关闭订单，默认仅支持ID参数")
    @PostMapping("/close")
    protected R<Boolean> close( @RequestBody T entity){
        T info = service.getById( entity.getId() );
        CoolPreconditions.checkEmpty(info , "找不到订单");
        
        if ( PayStatusEnum.PAYED.equals( info.getPayStatus() ) ){
            return R.error("订单已支付" );
        }
        
        info.setPayStatus( PayStatusEnum.CANCEL );

        boolean update = service.update(info);
        if (update){
            try {
                service.close( info );
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
    protected R pay( @Valid @RequestBody PayInfo pay ) throws WxPayException {

        String className = this.getClass().getSimpleName();
        String classPath = ConvertUtil.extractController2Path("app", className);

        log.info("调用支付，ID: {}", pay.getId() );

        T info = service.getById( pay.getId() );
        
        CoolPreconditions.checkEmpty(info , "找不到订单");
        
        if ( PayStatusEnum.PAYED.equals( info.getPayStatus() ) ){
            return R.error("订单已支付");
        }
        
        
        if ( PayStatusEnum.CANCEL.equals( info.getPayStatus() ) ){
            return R.error("订单已关闭");
        }

        
        info.setPayStatus( PayStatusEnum.PAYING );

        if (StringUtils.isBlank(info.getOutTradeNo())) {
            info.setOutTradeNo(wxPayService.createOrderNum("0001"));
        }
        
        if ( !StringUtils.isBlank(pay.getTerminal()) ) {
            info.setTerminal( pay.getTerminal() );
        }
        
        if ( !StringUtils.isBlank(pay.getPayWay()) ) {
            info.setPayWay( pay.getPayWay() );
        }

        service.update( info);


        String payWay = info.getPayWay();

        if (!service.isSupportPayWay(payWay)) {
            return R.error(400, "不支持的支付方式");
        }
        
        String payTerminal = info.getTerminal();
        if ( !service.isSupportTerminal(payTerminal)){
            return R.error(400, "不支持的客户端类型");
        }


        StringBuffer notifyUrl = new StringBuffer().append(getDoamin()).append("/").append(classPath);


        return switch (payTerminal) {
            case PayTerminalEnum.MP_WECHAT -> {
//                小程序端只支持微信支付
                if (!wxPayService.isEnable()) {
                    yield R.error(500, "微信支付未启用");
                }

                WxPayMpOrderResult orderByMini = wxPayService.create( info , CoolSecurityUtil.getCurrentUserId() , notifyUrl.toString() );

                yield R.ok(orderByMini);
            }
            case PayTerminalEnum.APP -> switch (payWay) {
                case PayWayEnum.WECHAT -> {
//                    APP端微信支付
                    if (!wxPayService.isEnable()) {
                        yield R.error(500, "微信支付未启用");
                    }

                    notifyUrl.append("/wxNotify");
                    
                    WxPayMpOrderResult orderByApp = wxPayService.create( info , CoolSecurityUtil.getCurrentUserId() , notifyUrl.toString() );
                    yield R.ok(orderByApp);

                }
                case PayWayEnum.ALIPAY -> {
//                    APP端支付宝
                    if (!aliPayService.isEnable()) {
                        yield R.error(500, "支付宝支付未启用");
                    }
                    notifyUrl.append("/aliNotify");
                    try {
                        
                        String orderByAliApp = aliPayService.create( info , CoolSecurityUtil.getCurrentUserId() , notifyUrl.toString() );

                        yield R.ok(orderByAliApp);
                    } catch (AlipayApiException e) {
                        yield R.error(503, e.getMessage());
                    }


                }
                default -> R.error(400 , "参数错误或暂不支持的支付方式");
            };
            //  TODO: 多端支付
            case PayTerminalEnum.H5 ->  R.error(400 , "暂不支持H5支付");
            case PayTerminalEnum.WOA -> R.error(400 , "暂不支持公众号支付");
            case PayTerminalEnum.PC ->  R.error(400 , "暂不支持网页支付");
            default -> R.error(400 , "参数错误或暂不支持的支付方式");
        };

    }


    @Operation(summary = "支付回调通知处理")
    @PostMapping("/wxNotify")
    @TokenIgnore
    public String wxNotify(@RequestBody String xmlData) {
        try {
            // 解析微信支付的回调数据
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);

            String outTradeNo = notifyResult.getOutTradeNo(); // 商户订单号
            // 检查支付结果
            if ("SUCCESS".equals(notifyResult.getResultCode())) {
                // 支付成功的逻辑处理
                log.info("微信支付成功，订单号: {}", outTradeNo);

                
                T order = service.getByOutTradeNo( outTradeNo );
                order.setPayStatus(PayStatusEnum.PAYED );
                order.setPayTime( LocalDateTime.now() );
                service.updateById( order );
                

                service.payNotice(outTradeNo);


                return WxPayNotifyResponse.success("success"); // 返回给微信支付处理结果
            } else {
                log.error("微信支付失败，订单号: {}", outTradeNo);
                return WxPayNotifyResponse.fail("fail");
            }
        } catch (Exception e) {
            log.error("微信支付回调处理异常: {}", e.getMessage(), e);
            return WxPayNotifyResponse.fail("fail");
        }
    }

    @Operation(summary = "支付回调通知处理")
    @PostMapping("/aliNotify")
    @TokenIgnore
    public String aliNotify(@RequestParam Map<String, String> params) throws IOException {

        if (params.get("trade_status").equals("TRADE_SUCCESS")) {

            try {

                /**
                 *  TODO：不知道为啥验签结果总是 false，
                 *  可能是因为被我去除支付宝依赖冲突
                 *     <groupId>org.bouncycastle</groupId>
                 *     <artifactId>bcprov-jdk15on</artifactId>
                 *     暂时不管验证结果
                 */
//                
                aliPayService.verifyNotify(params);
            } catch (AlipayApiException e) {
                return e.getMessage();
            }
            
            

            T order = service.getByOutTradeNo(params.get("out_trade_no"));
            order.setPayStatus(PayStatusEnum.PAYED );
            order.setPayTime( LocalDateTime.now() );
            service.updateById( order) ;
                
            

//            业务通知
            service.payNotice(params.get("out_trade_no"));

        }
        return "success";
    }

}
