package com.cool.core.base;

import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 每个用户只能有一条的数据，可以继承此接口
 * @param <S>
 * @param <T>
 */
public abstract class OnlyOneControllerAdapter<S extends BaseService<T>, T extends BaseEntity<T> & BelongingUserEntity> extends AppController<S,T> {


    @Operation(summary = "", description = "")
    @PostMapping("/add")
    protected R<Long> add(@Valid @RequestBody T requestParams) {
        return R.error("禁止使用此接口");
    }

    @Operation(summary = "", description = "")
    @PostMapping("/delete")
    protected R<Boolean> delete( @RequestBody T requestParams ) {
        return R.error("禁止使用此接口");
    }
    
    @Operation(summary = "修改", description = "修改")
    @PostMapping("/update")
    protected R<Boolean> update(@RequestBody T t) {
        Boolean modify = service.saveOrUpdate(t);
        return R.ok(modify);
    }
    

    @Operation(summary = "分页查询我的数据", description = "")
    @PostMapping("/myList")
    protected R<PageResult<T>> myList( @Valid @RequestBody PageParams<T> pageParams ) {
        pageParams.setSize(1);
        pageParams.setPage(1);
        T requestParams = pageParams.getParams();
        
        requestParams.setUserId(CoolSecurityUtil.getCurrentUserId());
        
        Page<T> TPage = this.service.myList( pageParams );
        return R.ok(pageResult(TPage));
    }
    
}
