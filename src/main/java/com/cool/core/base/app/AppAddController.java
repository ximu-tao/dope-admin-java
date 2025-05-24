package com.cool.core.base.app;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.base.IController;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppAddController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {

    @Operation(summary = "发布/新增/创建", description = "")
    @PostMapping("/add")
    default public R<T> add(@Valid @RequestBody T params) {
        Long userId = CoolSecurityUtil.getCurrentUserId();

        if (params instanceof BelongingUserEntity appEntity) {
            appEntity.setUserId(userId);
        }

        Long add = getService().add(params);
        return R.ok(params);
    }

}