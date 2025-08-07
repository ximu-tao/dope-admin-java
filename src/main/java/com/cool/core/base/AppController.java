package com.cool.core.base;

import cn.hutool.json.JSONObject;
import com.cool.core.base.app.*;
import jakarta.servlet.http.HttpServletRequest;

public abstract class AppController<S extends BaseService<T>, T extends BaseEntity<T>> extends BaseController<S, T> implements
        AppAddController<S, T>,
        AppDeleteController<S, T>,
        AppUpdateController<S, T>,
        AppInfoController<S, T>,
        AppMyInfoController<S, T>,
        AppPageController<S, T>,
        AppMyListController<S, T>,
        AppListController<S, T> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
    }
}
