package com.cool.plugin;

import cn.hutool.json.JSONUtil;
import com.cool.core.config.PluginJson;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.tencent.cloud.CosStsClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class TxCosService {

    private final PluginInfoService pluginInfoService;

    private Boolean enable = false;
    
    public Boolean isEnable() {
        return enable;
    }
    
    private PluginJson pluginJson;
    
    TxCosService(PluginInfoService pluginInfoService) {
        this.pluginInfoService = pluginInfoService;
        try {

            PluginInfoEntity byKey = pluginInfoService.getByKey("upload-cos");
            this.pluginJson = byKey.getPluginJson();
            this.enable = byKey.getStatus() == 1;
        } catch (Exception e) {
        }
    }

    public Object invokePlugin(String... params) {
        try {
            Map<String, Object> _config = this.pluginJson.getConfig();
            TreeMap<String, Object> config = new TreeMap();
            config.put("secretId", _config.get("accessKeyId"));
            config.put("secretKey", _config.get("accessKeySecret"));
            config.put("durationSeconds", 1800);
            config.put("region", _config.get("region"));
            config.put("bucket", _config.get("bucket"));
            config.put("allowPrefixes", new String[]{"_ALLOW_DIR_/*"});
            String[] allowActions = new String[]{"name/cos:PutObject", "name/cos:PostObject", "name/cos:InitiateMultipartUpload", "name/cos:ListMultipartUploads", "name/cos:ListParts", "name/cos:UploadPart", "name/cos:CompleteMultipartUpload"};
            config.put("allowActions", allowActions);
            String bucketName = (String) _config.get("bucket");
            String shortBucketName = bucketName.substring(0, bucketName.lastIndexOf("-"));
            String appId = bucketName.substring(bucketName.lastIndexOf("-") + 1);
            String raw_policy = String.format("{\n  \"version\": \"2.0\",\n  \"statement\": [\n    {\n      \"action\": %s,\n      \"effect\": \"allow\",\n      \"resource\": [\n        \"qcs::cos:%s:uid/%s:prefix//%s/%s/_ALLOW_DIR_/*\"\n      ]\n    }\n  ]\n}", JSONUtil.toJsonStr(allowActions), _config.get("region"), appId, appId, shortBucketName);
            config.put("policy", raw_policy);
            JSONObject credential = CosStsClient.getCredential(config);
            Map<String, Object> respMap = credential.toMap();
            respMap.put("url", _config.get("publicDomain"));
            return respMap;
        } catch (Exception e) {
            log.error("Error in invokePlugin: ", e);
            return null;
        }
    }
    
}
