package com.cool.modules.user.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AppController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.service.UserOauthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 第三方绑定信息
 */
@Tag(name = "第三方绑定信息", description = "第三方绑定信息")
@CoolRestController(api = { Apis.MY_LIST })
public class AppUserOauthController extends AppController<UserOauthService, UserOauthEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}