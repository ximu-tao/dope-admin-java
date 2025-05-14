package com.cool.plugin;

import com.cool.core.base.BasePaymentService;
import com.cool.core.pay.PayableEntity;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.cool.modules.user.service.UserOauthService;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WxPayService extends WxPayServiceImpl implements BasePaymentService {

    @Getter
    private Boolean enable = false;
    
    private WxPayService wxPayService;
    
    private final UserOauthService userOauthService;
    
    public WxPayService(PluginInfoService pluginInfoService, UserOauthService userOauthService) {
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
    public WxPayMpOrderResult create(PayableEntity entity, Long payerId, String notifyUrl, String returnUrl) throws WxPayException {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody( entity.getBody() );
        orderRequest.setOutTradeNo( entity.getOutTradeNo() );
        orderRequest.setTotalFee( (int)(entity.getTotal()*100) );
        
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl( notifyUrl );
        
        if (PayTerminalEnum.MP_WECHAT.equals( entity.getTerminal() )){
            orderRequest.setOpenid( userOauthService.getOpenid( payerId , PayWayEnum.WECHAT ,  PayTerminalEnum.MP_WECHAT ) );
            orderRequest.setTradeType( WxPayConstants.TradeType.JSAPI );
        }else if ( PayTerminalEnum.APP.equals( entity.getTerminal() ) ){
            orderRequest.setTradeType( WxPayConstants.TradeType.APP );
        }
        
        return this.createOrder( orderRequest );
    }
}
