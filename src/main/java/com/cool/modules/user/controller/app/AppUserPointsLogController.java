package com.cool.modules.user.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AppController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserPointsLogEntity;
import com.cool.modules.user.service.UserPointsLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户积分变更记录
 */
@Tag(name = "用户积分变更记录", description = "用户积分变更记录")
@CoolRestController(api = { Apis.MY_LIST })
public class AppUserPointsLogController extends AppController<UserPointsLogService, UserPointsLogEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}