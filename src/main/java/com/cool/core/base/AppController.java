package com.cool.core.base;

import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

public class AppController <S extends BaseService<T>, T extends BaseEntity<T>> extends BaseController<S,T> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        
    }
}
