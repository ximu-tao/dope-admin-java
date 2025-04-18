//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.cool.plugin;

import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TxSmsService {
    private String appId;
    private String secretId;
    private String secretKey;
    private String signName;
    private String template;


    private PluginInfoService pluginInfoService;

    private Boolean enable = false;

    public Boolean isEnable() {
        return enable;
    }

    public TxSmsService(PluginInfoService pluginInfoService) {
        try {
            this.pluginInfoService = pluginInfoService;

            PluginInfoEntity byKey = pluginInfoService.getByKey("sms-tx");
            Map<String, Object> config = byKey.getConfig();

            this.appId = (String) config.get("appId");
            this.secretId = (String) config.get("secretId");
            this.secretKey = (String) config.get("secretKey");
            this.signName = (String) config.get("signName");
            this.template = (String) config.get("template");

            this.enable = byKey.getStatus() == 1;
        } catch (Exception e) {
        }
    }

    public Map<String, Object> send(List<String> phone, Map<String, Object> params) {
        return this.send(phone, params, new HashMap());
    }

    public Map<String, Object> send(List<String> phones, Map<String, Object> params, Map<String, Object> config) {
        Map<String, Object> result = new HashMap();

        try {
            Credential cred = new Credential(this.secretId, this.secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(10);
            httpProfile.setWriteTimeout(10);
            httpProfile.setReadTimeout(10);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppId(this.appId);
            req.setSignName(config.getOrDefault("signName", this.signName).toString());
            req.setTemplateId(config.getOrDefault("template", this.template).toString());
            String[] templateParamSet = (String[]) params.values().toArray(new String[0]);
            req.setTemplateParamSet(templateParamSet);
            String[] phoneNumberSet = (String[]) phones.stream().map((phone) -> {
                Object var10000 = config.getOrDefault("countryCode", "86");
                return "+" + var10000 + phone;
            }).toArray((x$0) -> new String[x$0]);
            req.setPhoneNumberSet(phoneNumberSet);
            SendSmsResponse res = client.SendSms(req);
            result.put("response", SendSmsResponse.toJsonString(res));
        } catch (TencentCloudSDKException e) {
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


}
