package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserVipOrderEntity;
import com.cool.modules.user.service.UserVipOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 会员订单
 */
@Tag(name = "会员订单", description = "会员订单")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO, Apis.MY_LIST, Apis.MY_INFO})
public class AdminUserVipOrderController extends AdminController<UserVipOrderService, UserVipOrderEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}