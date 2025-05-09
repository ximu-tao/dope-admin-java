package com.cool.modules.user.service;

import com.cool.modules.user.controller.app.response.LoginResponse;

/**
 * 用户登录
 */
public interface UserLoginService {

    /**
     * 发送短信验证码
     * @param phone
     * @param captchaId
     * @param code
     */
   void smsCode(String phone, String captchaId, String code);

    /**
     * 手机号验证码登录
     * @param phone
     * @param smsCode
     */
    LoginResponse phoneVerifyCode(String phone, String smsCode);


    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的token
     */
    LoginResponse refreshToken(String refreshToken);
    /**
     * 小程序登录
     */
    LoginResponse mini(String code, String encryptedData, String iv);
    /**
     * 公众号登录
     */
    LoginResponse mp(String code);
    /**
     * 微信APP授权登录
     */
    LoginResponse wxApp(String code);

    /**
     * 一键手机号登录
     */
    LoginResponse uniPhone(String accessToken, String openid, String appId);
    /**
     * 绑定小程序手机号
     */
    LoginResponse miniPhone(String code, String encryptedData, String iv);

    /**
     * 密码登录
     */
    LoginResponse password(String phoneOrUsername, String password);
    
    
    
    LoginResponse register(String username, String password);
}
