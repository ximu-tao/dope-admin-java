package com.cool.core.base.app;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.base.IController;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppMyListController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {

    @Operation(summary = "分页查询我的数据", description = "")
    @PostMapping("/myList")
    default R<PageResult<T>> myList(@Valid @RequestBody PageParams<T> pageParams) {

        T params = pageParams.getParams();
        if (params instanceof BelongingUserEntity appEntity) {
            appEntity.setUserId(CoolSecurityUtil.getCurrentUserId());
        }

        Page<T> tPage = this.getService().pageWithRelationsForUser(null, pageParams.toPage(), getService().buildAppQueryWrapper(pageParams), pageParams.getWith(), CoolSecurityUtil.getCurrentUserId());

        return R.ok(PageResult.of(tPage));
    }
        
}