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

public interface AppDeleteController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {
    
    @Operation(summary = "删除", description = "默认仅支持ID删除，具体看子类实现")
    @PostMapping("/delete")
    default R<Boolean> delete(@RequestBody T t) {

        if (t.getId() == null || t.getId() <= 0) {
            CoolPreconditions.alwaysThrow("ID不能为空");
        }

        if (t instanceof BelongingUserEntity appEntity) {

            BelongingUserEntity byId = (BelongingUserEntity) getService().info(t.getId(), null);

            if (!byId.getUserId().equals(CoolSecurityUtil.getCurrentUserId())) {
                return R.error("不是你的数据");
            }
        }
        Boolean delete = getService().delete(t.getId());
        return R.ok(delete);
    }
    
}