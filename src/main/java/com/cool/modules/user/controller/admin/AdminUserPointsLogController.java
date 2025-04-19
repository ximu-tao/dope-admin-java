package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserPointsLogEntity;
import com.cool.modules.user.service.UserPointsLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户积分变更记录
 */
@Tag(name = "用户积分变更记录", description = "用户积分变更记录")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO, Apis.MY_LIST, Apis.MY_INFO})
public class AdminUserPointsLogController extends AdminController<UserPointsLogService, UserPointsLogEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}