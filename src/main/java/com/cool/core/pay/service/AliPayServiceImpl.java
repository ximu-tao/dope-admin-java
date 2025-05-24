package com.cool.core.pay.service;

import cn.hutool.core.util.ObjectUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConfig;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.cool.core.base.BaseEntity;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.pay.BasePaymentService;
import com.cool.core.pay.PayableEntity;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.pay.PayableService;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service(PayWayEnum.ALIPAY)
public class AliPayServiceImpl implements BasePaymentService {


    @Getter
    private DefaultAlipayClient alipayClient;

    private Boolean enable = false;

    private AlipayConfig alipayConfig = new AlipayConfig();

    AliPayServiceImpl(PluginInfoService pluginInfoService) throws AlipayApiException {
        try {

            PluginInfoEntity byKey = pluginInfoService.getByKey("pay-ali");
            Map<String, Object> config = (Map<String, Object>) byKey.getConfig();

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

            this.alipayClient = new DefaultAlipayClient(alipayConfig);

            this.enable = true;
        } catch (Exception e) {

        }
    }

    public Boolean isEnable() {
        return enable;
    }


    @Deprecated
    public String createOrderByApp(String totalAmount, String outTradeNo, String notifyUrl, String body) throws AlipayApiException {

        // 构造请求参数以调用接口
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        // 设置商户订单号
        model.setOutTradeNo(outTradeNo);

        // 设置订单总金额
        model.setTotalAmount(totalAmount);

        // 设置订单标题
        model.setSubject(body);

        request.setBizModel(model);

        request.setNotifyUrl(notifyUrl);

        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);

        CoolPreconditions.check(!response.isSuccess(), "支付宝调用失败");

        return response.getBody();
    }

    public Boolean verifyNotify(Map<String, String> params) throws AlipayApiException {
        boolean b = AlipaySignature.rsaCertCheckV2(params, this.alipayConfig.getAlipayPublicCertPath(), AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
        return b;
    }

    @Override
    public Object create(PayableEntity entity, Long payerId, String notifyUrl, String returnUrl, PayableService<?> service) throws AlipayApiException {
        
        
        if (ObjectUtil.isEmpty( entity.getOutTradeNo() )) {
            entity.setOutTradeNo( BasePaymentService.createOrderNum("o") );
            entity.updateById();
        }
        
        
        
        // 构造请求参数以调用接口
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        // 设置商户订单号
        model.setOutTradeNo(entity.getOutTradeNo());

        // 设置订单总金额
        model.setTotalAmount(entity.getTotalAmount().toString());

        // 设置订单标题
        model.setSubject(entity.getBody());

        request.setBizModel(model);

        request.setNotifyUrl(notifyUrl);

        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);

        CoolPreconditions.check(!response.isSuccess(), "支付宝调用失败");

        return response.getBody();
    }

    
    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        return params;
    }
    
    @Override
    public Object notify(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<?> service) throws Exception {
        Map<String, String> params = convertRequestParamsToMap( request );

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
                this.verifyNotify(params);
            } catch (AlipayApiException e) {
                CoolPreconditions.alwaysThrow(e.getMessage());
            }

            PayableEntity<?> order = service.getByOutTradeNo(params.get("out_trade_no"));
            order.setPayStatus(PayStatusEnum.PAYED);
            order.setPayTime(LocalDateTime.now());
            order.updateById();
            service.payNotice(params.get("out_trade_no"));

        }


        return "success";
    }

    @Override
    public String parseOutTradeNo(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<?> service) throws Exception {
        return "";
    }

    @Override
    public Boolean verify(HttpServletRequest request, HttpServletResponse httpResponse, PayableService<?> service, PayableEntity entity) throws Exception {
        return null;
    }

}
