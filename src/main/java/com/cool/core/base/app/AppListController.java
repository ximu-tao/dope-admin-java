package com.cool.core.base.app;

import com.cool.core.annotation.TokenIgnore;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.IController;
import com.cool.core.request.PageParams;
import com.cool.core.request.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AppListController<S extends BaseService<T>, T extends BaseEntity<T>> extends IController<S, T> {
    
    @TokenIgnore
    @Operation(summary = "查询所有数据", description = "")
    @PostMapping("/list")
    default R<List<T>> list(@Valid @RequestBody PageParams<T> pageParams) {

        List<T> TPage = this.getService().listWithRelations(null, getService().buildAppQueryWrapper(pageParams), pageParams.getWith());

        return R.ok(TPage);
    }
    
}