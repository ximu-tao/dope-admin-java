package com.cool.modules.user.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.enums.Apis;
import com.cool.modules.user.entity.UserVipInfoEntity;
import com.cool.modules.user.service.UserVipInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 会员开通配置
 */
@Tag(name = "会员开通配置", description = "会员开通配置")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO })
public class AdminUserVipInfoController extends AdminController<UserVipInfoService, UserVipInfoEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}