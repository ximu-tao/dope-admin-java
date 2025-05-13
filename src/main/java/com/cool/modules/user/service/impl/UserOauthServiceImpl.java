package com.cool.modules.user.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.cache.AuthStateRedisCache;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.mapper.UserOauthMapper;
import com.cool.modules.user.proxy.WxProxy;
import com.cool.modules.user.service.UserInfoService;
import com.cool.modules.user.service.UserOauthService;
import com.mybatisflex.core.query.QueryWrapper;
import com.xkcoding.http.config.HttpConfig;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.enums.scope.*;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import me.zhyd.oauth.utils.AuthScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 第三方绑定信息
 */
@Service
@RequiredArgsConstructor
public class UserOauthServiceImpl extends BaseServiceImpl<UserOauthMapper, UserOauthEntity> implements UserOauthService {
    
    
    @Lazy
    @Autowired
    PluginInfoService pluginInfoService;
    
    @Autowired
    private AuthStateRedisCache stateRedisCache;
    
    private final WxProxy wxProxy;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public UserOauthEntity loginByMini(String code, String encryptedData, String iv){
                // 获取 session
        WxMaJscode2SessionResult result = null;
        try {
            result = wxProxy.getSessionInfo(code);
            // 解密数据
            WxMaUserInfo wxMaUserInfo = wxProxy.getUserInfo(result.getSessionKey(), encryptedData, iv);

            if (ObjUtil.isNotEmpty(wxMaUserInfo)) {

                UserOauthEntity userOauthEntity = new UserOauthEntity();
                userOauthEntity.setProvider( PayWayEnum.WECHAT );
                userOauthEntity.setPlatform( PayTerminalEnum.MP_WECHAT );
                userOauthEntity.setOpenid( result.getOpenid() );
                userOauthEntity.setUnionid( result.getUnionid() );

                Map<String, Object> stringObjectMap = BeanUtil.beanToMap(wxMaUserInfo, true, true);
                
                userOauthEntity.setExtend( stringObjectMap );
                return getBySave( userOauthEntity );
            }
        } catch (WxErrorException e) {
            CoolPreconditions.alwaysThrow(e.getMessage(), e);
        }
        CoolPreconditions.alwaysThrow("获得小程序用户信息");
        return null;
    }

    @Override
    public String getOpenid(Long userId, String provider, String platform) {

        UserOauthEntity one = this.getOne(
                QueryWrapper.create()
                        .eq(UserOauthEntity::getUserId, userId)
                        .eq(UserOauthEntity::getProvider, provider)
                        .eq(UserOauthEntity::getPlatform, platform)
        );


        return one != null ? one.getOpenid() : "";
    }


    public UserOauthEntity getBySave( UserOauthEntity entity ){

        UserOauthEntity one = this.getOne(
                QueryWrapper.create()
                        .eq(UserOauthEntity::getOpenid, entity.getOpenid())
                        .eq(UserOauthEntity::getProvider, entity.getProvider())
                        .eq(UserOauthEntity::getPlatform, entity.getPlatform())
        );
        if ( ObjUtil.isEmpty(one) ){
            entity.setUserId(0L);
            super.save(entity);
            return entity;
        }
        return one;
    }
    
    
    private AuthConfig getAuthConfig( String source , List<String> scopes ){
                PluginInfoEntity oauth = pluginInfoService.getByKey("oauth");

        Map<String, Object> config = (Map<String, Object>) oauth.getConfig();

        Map<String, Object> providerConfig =  (Map<String, Object>) config.get(source);
        AuthConfig.AuthConfigBuilder authConfigBuilder = AuthConfig.builder()
                .clientId((String) providerConfig.get("clientId"))
                .clientSecret((String) providerConfig.get("clientSecret"))
                .redirectUri((String) config.get("redirect_domain") + "/app/user/oauth/callback/" + source);
        if ( providerConfig.containsKey("httpConfig") ){
            Map<String, Object> httpConfig = (Map<String, Object>) providerConfig.get("clientSecret");
            Map<String, Object> proxyConfig = (Map<String, Object>) httpConfig.get("proxy");
            
            Proxy proxy = null;
            int type = (int) proxyConfig.get("type");
            if ( type == 1 ){
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress( (String) proxyConfig.get("hostname"), (int) proxyConfig.get("port")));
            } else if ( type == 2 ) {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress( (String) proxyConfig.get("hostname"), (int) proxyConfig.get("port")));
            }

            authConfigBuilder.httpConfig(
                    HttpConfig.builder()
                            .timeout((Integer) httpConfig.get("timeout"))
                            .proxy(proxy)
                            .build()
            );
            
        }
        
        if ( scopes != null ){
            authConfigBuilder.scopes(scopes);
        }
        
        return authConfigBuilder.build();
    }
    
    
    @Override
    public AuthRequest getAuthRequest(String source) {

        AuthRequest authRequest = null;
        switch (source.toLowerCase()) {
            case "dingtalk":
                authRequest = new AuthDingTalkRequest(  this.getAuthConfig(source, null) );
                break;
            case "baidu":
                authRequest = new AuthBaiduRequest( this.getAuthConfig(source, Arrays.asList(
                                AuthBaiduScope.BASIC.getScope(),
                                AuthBaiduScope.SUPER_MSG.getScope(),
                                AuthBaiduScope.NETDISK.getScope()
                        )) );
                break;
            case "github":
                authRequest = new AuthGithubRequest( this.getAuthConfig(source, null), stateRedisCache);
                break;
            case "gitee":
                authRequest = new AuthGiteeRequest( this.getAuthConfig( source, AuthScopeUtils.getScopes(AuthGiteeScope.values()) ), stateRedisCache);
                break;
            case "weibo":
                authRequest = new AuthWeiboRequest( this.getAuthConfig( source,Arrays.asList(
                                AuthWeiboScope.EMAIL.getScope(),
                                AuthWeiboScope.FRIENDSHIPS_GROUPS_READ.getScope(),
                                AuthWeiboScope.STATUSES_TO_ME_READ.getScope()
                        )));
                break;
            case "coding":
                authRequest = new AuthCodingRequest(AuthConfig.builder()
                        .clientId("")
                        .clientSecret("")
                        .redirectUri("http://dblog-web.zhyd.me/oauth/callback/coding")
                        .domainPrefix("")
                        .scopes(Arrays.asList(
                                AuthCodingScope.USER.getScope(),
                                AuthCodingScope.USER_EMAIL.getScope(),
                                AuthCodingScope.USER_PHONE.getScope()
                        ))
                        .build());
                break;
            case "oschina":
                authRequest = new AuthOschinaRequest( this.getAuthConfig(source, null));
                break;
            case "alipay":
                // 支付宝在创建回调地址时，不允许使用localhost或者127.0.0.1，所以这儿的回调地址使用的局域网内的ip
                authRequest = new AuthAlipayRequest(AuthConfig.builder()
                        .clientId("")
                        .clientSecret("")
                        .alipayPublicKey("")
                        .redirectUri("https://www.zhyd.me/oauth/callback/alipay")
                        .build());
                break;
            case "qq":
                authRequest = new AuthQqRequest( this.getAuthConfig(source, null));
                break;
            case "wechat_open":
                authRequest = new AuthWeChatOpenRequest( this.getAuthConfig(source, null));
                break;
            case "csdn":
                authRequest = new AuthCsdnRequest( this.getAuthConfig(source, null));
                break;
            case "taobao":
                authRequest = new AuthTaobaoRequest(  this.getAuthConfig(source, null));
                break;
            case "google":
                authRequest = new AuthGoogleRequest( this.getAuthConfig(source, AuthScopeUtils.getScopes(AuthGoogleScope.USER_EMAIL, AuthGoogleScope.USER_PROFILE, AuthGoogleScope.USER_OPENID)));
                break;
            case "facebook":
                authRequest = new AuthFacebookRequest( this.getAuthConfig(source, AuthScopeUtils.getScopes(AuthFacebookScope.values())) );
                break;
            case "douyin":
                authRequest = new AuthDouyinRequest( this.getAuthConfig(source, null) );
                break;
            case "linkedin":
                authRequest = new AuthLinkedinRequest( this.getAuthConfig(source, null) );
                break;
            case "microsoft":
                authRequest = new AuthMicrosoftRequest( this.getAuthConfig(source, Arrays.asList(
                                AuthMicrosoftScope.USER_READ.getScope(),
                                AuthMicrosoftScope.USER_READWRITE.getScope(),
                                AuthMicrosoftScope.USER_READBASIC_ALL.getScope(),
                                AuthMicrosoftScope.USER_READ_ALL.getScope(),
                                AuthMicrosoftScope.USER_READWRITE_ALL.getScope(),
                                AuthMicrosoftScope.USER_INVITE_ALL.getScope(),
                                AuthMicrosoftScope.USER_EXPORT_ALL.getScope(),
                                AuthMicrosoftScope.USER_MANAGEIDENTITIES_ALL.getScope(),
                                AuthMicrosoftScope.FILES_READ.getScope()
                        ) ) );
                break;
            case "mi":
                authRequest = new AuthMiRequest( this.getAuthConfig(source, null) );
                break;
            case "toutiao":
                authRequest = new AuthToutiaoRequest( this.getAuthConfig(source, null) );
                break;
            case "teambition":
                authRequest = new AuthTeambitionRequest( this.getAuthConfig(source, null) );
                break;
            case "pinterest":
                authRequest = new AuthPinterestRequest( this.getAuthConfig(source, null) );
                break;
            case "renren":
                authRequest = new AuthRenrenRequest( this.getAuthConfig(source, null) );
                break;
            case "stack_overflow":
                authRequest = new AuthStackOverflowRequest(AuthConfig.builder()
                        .clientId("")
                        .clientSecret("((")
                        .redirectUri("http://localhost:8443/oauth/callback/stack_overflow")
                        .stackOverflowKey("")
                        .build());
                break;
            case "huawei":
                authRequest = new AuthHuaweiRequest(  this.getAuthConfig(source, Arrays.asList(
                                AuthHuaweiScope.BASE_PROFILE.getScope(),
                                AuthHuaweiScope.MOBILE_NUMBER.getScope(),
                                AuthHuaweiScope.ACCOUNTLIST.getScope(),
                                AuthHuaweiScope.SCOPE_DRIVE_FILE.getScope(),
                                AuthHuaweiScope.SCOPE_DRIVE_APPDATA.getScope()
                        )));
                break;
            case "wechat_enterprise":
                authRequest = new AuthWeChatEnterpriseQrcodeRequest(AuthConfig.builder()
                        .clientId("")
                        .clientSecret("")
                        .redirectUri("http://justauth.cn/oauth/callback/wechat_enterprise")
                        .agentId("1000003")
                        .build());
                break;
            case "kujiale":
                authRequest = new AuthKujialeRequest(  this.getAuthConfig(source, null)  );
                break;
            case "gitlab":
                authRequest = new AuthGitlabRequest(  this.getAuthConfig(source, AuthScopeUtils.getScopes(AuthGitlabScope.values())) );
                break;
            case "meituan":
                authRequest = new AuthMeituanRequest(  this.getAuthConfig(source, null)  );
                break;
            case "eleme":
                authRequest = new AuthElemeRequest(  this.getAuthConfig(source, null)  );
                break;
            case "twitter":
                authRequest = new AuthTwitterRequest(  this.getAuthConfig(source, null)  );
                break;
            case "wechat_mp":
                authRequest = new AuthWeChatMpRequest(  this.getAuthConfig(source, null)  );
                break;
            case "aliyun":
                authRequest = new AuthAliyunRequest(  this.getAuthConfig(source, null)  );
                break;
            case "xmly":
                authRequest = new AuthXmlyRequest( this.getAuthConfig(source, null) );
                break;
            case "feishu":
                authRequest = new AuthFeishuRequest( this.getAuthConfig(source, null) );
                break;
            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException("未获取到有效的Auth配置");
        }
        return authRequest;
        
        
    }

    @Override
    public UserInfoEntity loginByOauth(AuthUser user) {

        UserOauthEntity userOauthEntity = new UserOauthEntity();
        userOauthEntity.setProvider( user.getSource() );
        userOauthEntity.setPlatform( "" );
        userOauthEntity.setOpenid( user.getUuid() );
        userOauthEntity.setUnionid( user.getUuid() );

        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(user, true, true);
        userOauthEntity.setExtend(stringObjectMap);

        UserOauthEntity bySave = this.getBySave(userOauthEntity);
        
        if (NumberUtil.equals( bySave.getUserId() , (Long)0L )){
            UserInfoEntity userInfo = new UserInfoEntity();

            userInfo.setNickName( user.getNickname() );
            userInfo.setAvatarUrl( user.getAvatar() );
            userInfo.setStatus( 1 );

            Long add = userInfoService.add(userInfo);
            
            bySave.setUserId( add );
            
            return userInfo;
        }else {
            return userInfoService.info(  bySave.getUserId() , null );
        }
    }
}