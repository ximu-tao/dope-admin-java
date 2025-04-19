package com.cool.plugin;

import cn.hutool.core.lang.Assert;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AliPayService  {
    
    
    @Getter
    private DefaultAlipayClient alipayClient;
    
    private Boolean enable = false;
    
    AliPayService(PluginInfoService pluginInfoService) throws AlipayApiException {
        try {
            
            PluginInfoEntity byKey = pluginInfoService.getByKey("pay-ali");
            Map<String, Object> config = (Map<String, Object>) byKey.getConfig();
            AlipayConfig alipayConfig = new AlipayConfig();
            //设置网关地址
            alipayConfig.setServerUrl((String) config.get("server_url"));
            //设置应用APPID
            alipayConfig.setAppId((String) config.get("app_id"));
            //设置应用私钥
            alipayConfig.setPrivateKey((String) config.get("private_key"));
            //设置应用公钥证书路径
            alipayConfig.setAppCertPath((String) config.get("app_cert_public_key"));
            //设置支付宝公钥证书路径
            alipayConfig.setAlipayPublicCertPath((String) config.get("ali_public_key"));
            //设置支付宝根证书路径
            alipayConfig.setRootCertPath((String) config.get("alipay_root_cert"));
            //设置请求格式，固定值json
            alipayConfig.setFormat("json");
            //设置字符集
            alipayConfig.setCharset("UTF-8");
            //设置签名类型
            alipayConfig.setSignType("RSA2");
            
            this.alipayClient = new DefaultAlipayClient( alipayConfig );
            
            this.enable = true;
        } catch (Exception e) {

        }
    }
    
    public Boolean isEnable() {
        return enable;
    }

}
