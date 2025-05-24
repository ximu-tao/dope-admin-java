package com.cool.core.base.app;


import cn.hutool.core.util.ObjectUtil;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.IController;
import com.cool.core.request.OneParams;
import com.cool.core.request.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppInfoController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {
    
    @TokenIgnore
    @Operation(summary = "数据详情", description = "")
    @PostMapping("/info")
    default R<T> info(@Valid @RequestBody OneParams oneParams) {

        T byId = getService().info(oneParams.getId(), oneParams.getWith());

        if (ObjectUtil.isEmpty(byId)) {
            return R.error("找不到数据");
        }

        return R.ok(byId);
    }
    
}