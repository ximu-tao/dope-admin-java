package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.service.UserSecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户重要数据
 */
@Tag(name = "用户重要数据", description = "用户重要数据")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO, Apis.MY_LIST, Apis.MY_INFO})
public class AdminUserSecurityController extends AdminController<UserSecurityService, UserSecurityEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}