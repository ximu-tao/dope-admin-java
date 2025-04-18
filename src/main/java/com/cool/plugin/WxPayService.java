package com.cool.plugin;

import cn.hutool.core.util.RandomUtil;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
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
public class WxPayService extends WxPayServiceImpl {

    @Getter
    private Boolean enable = false;
    
    private WxPayService wxPayService;
    
    public WxPayService(PluginInfoService pluginInfoService) {
        try {
            
            PluginInfoEntity byKey = pluginInfoService.getByKey("pay-wx");
            
            Map<String, Object> pluginIConfig = byKey.getConfig();
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
    
    
    /**
    * 生成订单号，基于时间戳+唯一字符串+随机数+可选的子ID
    *
    * @param subId 可选，如你的订单ID, 或者用户ID的一些组合
    * @return 订单号
    */
    public String createOrderNum( String subId ) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = RandomUtil.randomString(8);
        int randomNumber = 666;
        return timestamp + randomString + randomNumber + (subId != null ? subId : "");
    }
    
    
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
}
