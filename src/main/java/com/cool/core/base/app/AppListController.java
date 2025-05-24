package com.cool.core.base.app;

import com.cool.core.annotation.TokenIgnore;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.IController;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppListController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {
    
    @TokenIgnore
    @Operation(summary = "分页查询数据", description = "")
    @PostMapping("/list")
    default R<PageResult<T>> list(@Valid @RequestBody PageParams<T> pageParams) {

        Page<T> TPage = this.getService().pageWithRelations(null, pageParams.toPage(), getService().buildAppQueryWrapper(pageParams), pageParams.getWith());

        return R.ok(PageResult.of(TPage));
    }
    
}