package com.cool.modules.user.controller.app.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginParam {

    /*******小程序/公众号/微信APP授权 登录*******/
    @Schema(description = "（小程序/公众号/微信APP授权）")
    private String code;

    @Schema(description = "（小程序/公众号/微信APP授权）")
    private String encryptedData;

    @Schema(description = "（小程序/公众号/微信APP授权）")
    private String iv;


    /*******手机号登录*******/
    @Schema(description = "手机号（验证码登录/密码登录（密码登录时 手机号、用户名 二选一））")
    private String phone;

    @Schema(description = "验证码（验证码登录）")
    private String smsCode;


    /*******一键手机号登录*******/
    private String access_token;

    private String openid;

    private String appId;


    /*******密码登录*******/
    @Schema(description = "用户名（密码登录时 手机号、用户名 二选一）")
    private String username;
    
    
    @Schema(description = "密码登录时 手机号、用户名 都行")
    private String account;
    
    
    @Schema(description = "密码,密码登录")
    private String password;
}
