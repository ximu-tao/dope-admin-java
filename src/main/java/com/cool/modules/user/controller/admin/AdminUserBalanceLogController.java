package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserBalanceLogEntity;
import com.cool.modules.user.service.UserBalanceLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户余额变更记录
 */
@Tag(name = "用户余额变更记录", description = "用户余额变更记录")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO, Apis.MY_LIST, Apis.MY_INFO})
public class AdminUserBalanceLogController extends AdminController<UserBalanceLogService, UserBalanceLogEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}