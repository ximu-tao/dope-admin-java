package com.cool.core.pay.service;

import cn.hutool.core.util.ObjectUtil;
import com.cool.core.base.BaseEntity;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.pay.BasePaymentService;
import com.cool.core.pay.PayableEntity;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.pay.PayableService;
import com.cool.core.util.BodyReaderHttpServletRequestWrapper;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.cool.modules.user.service.UserOauthService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service( PayWayEnum.WECHAT )
public class CoolWxPayServiceImpl extends WxPayServiceImpl implements BasePaymentService {

    @Getter
    private Boolean enable = false;
    
    private WxPayService wxPayService;
    
    private final UserOauthService userOauthService;
    
    public CoolWxPayServiceImpl(PluginInfoService pluginInfoService, UserOauthService userOauthService) {
        this.userOauthService = userOauthService;
        try {
            
            PluginInfoEntity byKey = pluginInfoService.getByKey("pay-wx");
            
            Map<String, Object> pluginIConfig = (Map<String, Object>) byKey.getConfig();
            WxPayConfig config = new WxPayConfig();
            config.setAppId( pluginIConfig.get("appid").toString() );
            config.setMchId( pluginIConfig.get("mchid").toString()  );
            config.setMchKey( pluginIConfig.get("key").toString()  );
            config.setKeyPath( pluginIConfig.get("privateKeyPath").toString()  );
            config.setPrivateCertPath( pluginIConfig.get("privateCertPath").toString()  );
    
            this.setConfig(config);
            
            this.enable = byKey.getStatus()==1;

            wxPayService = this;
        }catch (Exception e){
            
        }
    }

    public Boolean isEnable() {
        return enable;
    }

    /**
     * 获得微信支付实例
     *
     * @return 微信支付实例
     */
    public WxPayService getInstance() {
        return wxPayService;
    }
    
    
    
    
    @Deprecated
    public WxPayMpOrderResult createOrderByMini(Integer totalFee , String outTradeNo , String tradeType , String notifyUrl , String body, String openid) 
            throws WxPayException {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody( body );
        orderRequest.setOutTradeNo( outTradeNo );
        orderRequest.setTotalFee( totalFee );//元转成分
        orderRequest.setOpenid( openid );
        orderRequest.setTradeType( tradeType );
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl( notifyUrl );
        return this.createOrder( orderRequest );
        
        
    }
    
    
    @Deprecated
    public WxPayMpOrderResult createOrderByApp(Integer totalFee , String outTradeNo, String notifyUrl , String body ) 
            throws WxPayException {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody( body );
        orderRequest.setOutTradeNo( outTradeNo );
        orderRequest.setTotalFee( totalFee );//元转成分
        orderRequest.setTradeType( WxPayConstants.TradeType.APP );
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl( notifyUrl );
        return this.createOrder( orderRequest );
        
        
    }
    
    
    



    @Override
    public <T extends BaseEntity<T> & PayableEntity<T>> Object create(T entity, Long payerId, String notifyUrl, String returnUrl, PayableService<T> service) throws Exception {

        if (ObjectUtil.isEmpty( entity.getOutTradeNo() )) {
            entity.setOutTradeNo( BasePaymentService.createOrderNum("o") );
            service.update( entity );
        }
        
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody( entity.getBody() );
        orderRequest.setOutTradeNo( entity.getOutTradeNo() );
        orderRequest.setTotalFee( (int)(entity.getTotal()*100) );
        
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl( notifyUrl );
        
        if (PayTerminalEnum.MP_WECHAT.equals( entity.getTerminal() )){
            String openid = userOauthService.getOpenid(payerId, PayWayEnum.WECHAT, PayTerminalEnum.MP_WECHAT);
            CoolPreconditions.checkEmpty( openid , "用户未绑定OpenId");
            orderRequest.setOpenid( openid );
            orderRequest.setTradeType( WxPayConstants.TradeType.JSAPI );
        }else if ( PayTerminalEnum.APP.equals( entity.getTerminal() ) ){
            orderRequest.setTradeType( WxPayConstants.TradeType.APP );
        }else {
            CoolPreconditions.alwaysThrow("不支持的支付渠道");
        }
        
        return this.createOrder( orderRequest );
    }
    

    @Override
    public <T extends BaseEntity<T> & PayableEntity<T>> Object notify(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<T> service) throws Exception {
        
        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        String body = requestWrapper.getBodyString(requestWrapper);
        
        
        try {
            // 解析微信支付的回调数据
            WxPayOrderNotifyResult notifyResult = this.parseOrderNotifyResult(body);

            String outTradeNo = notifyResult.getOutTradeNo(); // 商户订单号
            // 检查支付结果
            if ("SUCCESS".equals(notifyResult.getResultCode())) {
                // 支付成功的逻辑处理
                log.info("微信支付成功，订单号: {}", outTradeNo);
                
                T order = service.getByOutTradeNo(outTradeNo);
                order.setPayStatus(PayStatusEnum.PAYED );
                order.setPayTime( LocalDateTime.now() );
                service.update( order );
                
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

    @Override
    public <T extends BaseEntity<T> & PayableEntity<T>> String parseOutTradeNo(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<T> service) throws Exception {
        return "";
    }

    @Override
    public <T extends BaseEntity<T> & PayableEntity<T>> Boolean verify(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<T> service, T entity) throws Exception {
        return null;
    }
}
