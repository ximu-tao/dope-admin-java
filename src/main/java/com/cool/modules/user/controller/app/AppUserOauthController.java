package com.cool.modules.user.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AppController;
import com.cool.core.enums.Apis;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.service.UserOauthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录相关
 */
@Slf4j
@Tag(name = "第三方登录相关", description = "第三方登录相关")
@CoolRestController(api = { Apis.MY_LIST })
public class AppUserOauthController extends AppController<UserOauthService, UserOauthEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }


    @Override
    @PostMapping("/myList")
    @Operation(summary = "分页查询我的第三方账号绑定账号", description = "")
    protected R<PageResult<UserOauthEntity>> myList(PageParams<UserOauthEntity> pageParams, JSONObject requestParams) {
        return super.myList(pageParams, requestParams);
    }
    
    
    @RequestMapping("/{source}")
    @Operation( summary = "第三方授权登录", description = "source 第三方平台名称 google、twitter、facebook 等 ")
    @ResponseBody
    public void renderAuth(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        log.info("进入render：" + source);
        AuthRequest authRequest = service.getAuthRequest(source);
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        log.info(authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：https://{域名}/oauth/callback/github
     */
    @RequestMapping("/callback/{source}")
    public ModelAndView login(@PathVariable("source") String source, AuthCallback callback, HttpServletRequest request) {
        log.info("进入callback：" + source + " callback params：" + com.alibaba.fastjson.JSONObject.toJSONString(callback));
        AuthRequest authRequest = service.getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        log.info(com.alibaba.fastjson.JSONObject.toJSONString(response));

        if (response.ok()) {

            UserInfoEntity userInfoEntity = service.loginByOauth(response.getData());


            return new ModelAndView("redirect:/users");
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("errorMsg", response.getMsg());

        return new ModelAndView("error", map);
    }
}