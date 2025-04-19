package com.cool.modules.user.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AppController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.service.UserSecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户重要数据
 */
@Tag(name = "用户重要数据", description = "用户重要数据")
@CoolRestController(api = { Apis.MY_LIST })
public class AppUserSecurityController extends AppController<UserSecurityService, UserSecurityEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}