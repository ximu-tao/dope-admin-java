package com.cool.core.pay.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.base.BasePaymentService;
import com.cool.core.pay.PayableEntity;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.paypal.PayPalApi;
import com.ijpay.paypal.PayPalApiConfig;
import com.ijpay.paypal.PayPalApiConfigKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PayPalPayWayServiceImpl implements BasePaymentService {
    
    private final PluginInfoService pluginInfoService;
    
    PayPalPayWayServiceImpl(PluginInfoService pluginInfoService){
        this.pluginInfoService = pluginInfoService;
    }
    
    
    public PayPalApiConfig getConfig() {
        PluginInfoEntity pay = pluginInfoService.getByKey("pay");
        Map<String,Object> payConfig = (Map<String, Object>) pay.getConfig();
        Map<String,Object> payPalConfig = (Map<String, Object>) payConfig.get("PayPal");
        

        PayPalApiConfig config = new PayPalApiConfig();
        config.setClientId( payPalConfig.get("clientId").toString() );
        
        config.setSecret( payPalConfig.get("secret").toString() );
        config.setDomain( payPalConfig.get("domain").toString() );
        config.setSandBox((Boolean) payPalConfig.get("domain"));
        
        PayPalApiConfigKit.setThreadLocalApiConfig(config);
        return config;
    }

    @Override
    public Object create(PayableEntity entity, Long payerId, String notifyUrl, String returnUrl) throws Exception {
		try {
			PayPalApiConfig config = getConfig();

			//参数请求参数文档 https://developer.paypal.com/docs/api/orders/v2/#orders_create
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("intent", "CAPTURE");

			ArrayList<Map<String, Object>> list = new ArrayList<>();

			Map<String, Object> amount = new HashMap<>();
			amount.put("currency_code", "USD");
			amount.put("value", "0.01");

			Map<String, Object> itemMap = new HashMap<>();
			itemMap.put("amount", amount);

			list.add(itemMap);

			dataMap.put("purchase_units", list);

			Map<String, String> card = new HashMap<>();
			card.put("name", "test buyer");
			card.put("number", "4231220385792723");
			card.put("security_code", "123");
			card.put("expiry", "2029-07");

			Map<String, String> experienceContext = new HashMap<>();
			experienceContext.put("cancel_url", config.getDomain().concat(""));
			experienceContext.put("return_url", config.getDomain().concat(returnUrl));
			Map<String, Map<String,String>> paymentSource = new HashMap<>();
			paymentSource.put("experience_context",experienceContext);
			paymentSource.put("card",card);

//			dataMap.put("payment_source", paymentSource);

			String data = JSONUtil.toJsonStr(dataMap);
			log.info(data);
			IJPayHttpResponse resData = PayPalApi.createOrder(config, data);
			log.info(resData.toString());
			if (resData.getStatus() == 201) {
				String resultStr = resData.getBody();

				JSONObject jsonObject = JSONUtil.parseObj(resultStr);
				
				log.info(jsonObject.toString());
				
				JSONArray links = jsonObject.getJSONArray("links");
				for (int i = 0; i < links.size(); i++) {
					JSONObject item = links.getJSONObject(i);
					String rel = item.getStr("rel");
					String href = item.getStr("href");
					if ("approve".equalsIgnoreCase(rel)) {
						return href;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
			
		}
		return null;
    }
}
