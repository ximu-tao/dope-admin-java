package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.service.UserOauthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 第三方绑定信息
 */
@Tag(name = "第三方绑定信息", description = "第三方绑定信息")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO, Apis.MY_LIST, Apis.MY_INFO})
public class AdminUserOauthController extends AdminController<UserOauthService, UserOauthEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}