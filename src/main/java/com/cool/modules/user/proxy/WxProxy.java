package com.cool.modules.user.proxy;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpMapConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WxProxy {
    
    @Lazy
    @Autowired
    PluginInfoService pluginInfoService;
    
    
    private WxMaService wxMaService;
    private WxMpService wxMpService;
    
    private Boolean inited = false;
    
    public void init(){
        if ( this.inited ) return;

        PluginInfoEntity byKey = pluginInfoService.getByKey("wx");
        CoolPreconditions.checkEmpty( byKey , "插件参数未设置", new Object[0]);
        Map<String, Object> config = byKey.getConfig();

        JSONObject jsonObject = JSONUtil.parseObj(config);
        JSONObject miniAppConfig = (JSONObject)jsonObject.get("MiniApp", JSONObject.class);
        if (ObjUtil.isNotEmpty(miniAppConfig)) {
            this.wxMaService = new WxMaServiceImpl();
            this.wxMaService.setWxMaConfig((WxMaConfig)miniAppConfig.toBean(WxMaDefaultConfigImpl.class));
        }

        JSONObject officialAccountConfig = (JSONObject)jsonObject.get("OfficialAccount", JSONObject.class);
        if (ObjUtil.isNotEmpty(officialAccountConfig)) {
            this.wxMpService = new WxMpServiceImpl();
            this.wxMpService.setWxMpConfigStorage((WxMpConfigStorage)officialAccountConfig.toBean(WxMpMapConfigImpl.class));
        }
        
        this.inited = true;
    }
    
    public WxMaService getWxMaService() {
        this.init();
        return this.wxMaService;
    }
    public WxMpService getWxMpService() {
        this.init();
        return this.wxMpService;
    }

    public WxMaJscode2SessionResult getSessionInfo(String jsCode) throws WxErrorException {
        return getWxMaService().getUserService()
            .getSessionInfo(jsCode);
    }

    public WxMaPhoneNumberInfo getPhoneNumber(String jsCode) throws WxErrorException {
        return getWxMaService().getUserService()
            .getPhoneNumber(jsCode);
    }

    public WxMaUserInfo getUserInfo(String sessionKey, String encryptedData, String ivStr) throws WxErrorException {
        return getWxMaService().getUserService()
            .getUserInfo(sessionKey, encryptedData, ivStr);
    }

}
