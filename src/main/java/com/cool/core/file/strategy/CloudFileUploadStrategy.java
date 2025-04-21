package com.cool.core.file.strategy;

import com.cool.core.config.FileModeEnum;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.plugin.TxCosService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("cloudFileUploadStrategy")
public class CloudFileUploadStrategy implements FileUploadStrategy {

    @Autowired
    TxCosService txCosService; 
    
    @Override
    public Object upload(MultipartFile[] files, HttpServletRequest request, PluginInfoEntity pluginInfoEntity)
            throws IOException {

        String key = pluginInfoEntity.getKey();
        if ( "upload-cos".equals(key) ) {
            return txCosService.invokePlugin( key );
        }

        return CoolPluginInvokers.invokePlugin(pluginInfoEntity.getKey());
    }

    @Override
    public Map<String, String> getMode(String key) {
        try{
            Object mode = CoolPluginInvokers.invoke(key, "getMode");
            if (Objects.nonNull(mode)) {
                return (Map) mode;
            }
        } catch (Exception ignore){}
        return Map.of("mode", FileModeEnum.CLOUD.value(),
            "type", FileModeEnum.CLOUD.type());
    }
}
