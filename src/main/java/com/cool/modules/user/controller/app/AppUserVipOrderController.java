package com.cool.modules.user.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AppController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserVipOrderEntity;
import com.cool.modules.user.service.UserVipOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 会员订单
 */
@Tag(name = "会员订单", description = "会员订单")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.MY_LIST })
public class AppUserVipOrderController extends AppController<UserVipOrderService, UserVipOrderEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}