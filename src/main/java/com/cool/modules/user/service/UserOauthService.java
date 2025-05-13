package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.entity.UserOauthEntity;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;

/**
 * 第三方绑定信息
 */
public interface UserOauthService extends BaseService<UserOauthEntity> {

    /**
     * 微信小程序登录
     */
    UserOauthEntity loginByMini(String code, String encryptedData, String iv);


    /**
     * 获取 OpenID
     */
    String getOpenid( Long userId , String provider, String platform);

    AuthRequest getAuthRequest(String source);
    
    
    
    UserInfoEntity loginByOauth(AuthUser user );
}
