package com.cool.core.base.app;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.base.IController;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppUpdateController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {

    @Operation(summary = "修改", description = "修改")
    @PostMapping("/update")
    default R<Boolean> update(@RequestBody T t) {

        if (t.getId() == null || t.getId() <= 0) {
            CoolPreconditions.alwaysThrow("ID不能为空");
        }

        if (t instanceof BelongingUserEntity) {
            BelongingUserEntity byId = (BelongingUserEntity) getService().info(t.getId(), null);

            if (!byId.getUserId().equals(CoolSecurityUtil.getCurrentUserId())) {
                return R.error("不是你的数据");
            }

            ((BelongingUserEntity) t).setUserId(CoolSecurityUtil.getCurrentUserId());
        }

        Boolean modify = getService().update(t);
        return R.ok(modify);
    }
    
}