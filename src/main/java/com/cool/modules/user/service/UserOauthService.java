package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserOauthEntity;

/**
 * 第三方绑定信息
 */
public interface UserOauthService extends BaseService<UserOauthEntity> {

    /**
     * 微信小程序登录
     */
    UserOauthEntity loginByMini(String code, String encryptedData, String iv);
}
