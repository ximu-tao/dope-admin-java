package com.cool.core.base;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public abstract class AppController <S extends BaseService<T>, T extends BaseEntity<T>> extends BaseController<S,T> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        
    }
    
    
    
//    add  delete   update  info   list  page   lists   details
    
    @Operation(summary = "发布/新增/创建", description = "")
    @PostMapping("/add")
    protected R<Long> add(@Valid @RequestBody T requestParams) {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        
        if (requestParams instanceof BelongingUserEntity appEntity){
            appEntity.setUserId(userId);
        }
        
        Long add = service.create(requestParams);
        return R.ok(add);
    }

    @Operation(summary = "删除", description = "默认仅支持ID删除，具体看子类实现")
    @PostMapping("/delete")
    protected R<Boolean> delete( @RequestBody T requestParams ) {
        if (requestParams instanceof BelongingUserEntity appEntity){
            appEntity.setUserId( CoolSecurityUtil.getCurrentUserId() );
        }
        Boolean delete = service.delete(requestParams);
        return R.ok(delete);
    }
    
    @Operation(summary = "修改", description = "修改")
    @PostMapping("/update")
    protected R<Boolean> update(@RequestBody T t) {
        if (t instanceof BelongingUserEntity appEntity){
            appEntity.setUserId( CoolSecurityUtil.getCurrentUserId() );
        }
        
        Boolean modify = service.modify(t);
        return R.ok(modify);
    }
    
    
    @TokenIgnore
    @Operation(summary = "分页查询数据", description = "")
    @PostMapping("/list")
    protected R<PageResult<T>> list( @Valid @RequestBody PageParams<T> pageParams ) {
        
        Page<T> TPage = this.service.lists( pageParams );

        return R.ok(pageResult(TPage));
    }
    
    
    @Operation(summary = "分页查询我的数据", description = "")
    @PostMapping("/myList")
    protected R<PageResult<T>> myList( @Valid @RequestBody PageParams<T> pageParams ) {
        
        T requestParams = pageParams.getParams();
        if (requestParams instanceof BelongingUserEntity appEntity){
            appEntity.setUserId(CoolSecurityUtil.getCurrentUserId());
        }
        
        Page<T> TPage = this.service.myList( pageParams );
        return R.ok(pageResult(TPage));
    }
    
    @TokenIgnore
    @Operation(summary = "数据详情", description = "")
    @PostMapping("/info")
    protected R<T> info( Long id ){
        T byId = service.details(id);
        
        if (ObjectUtil.isEmpty( byId )){
            return R.error( "找不到数据"  );
        }

        return R.ok(byId);
    }
    

    @Operation(summary = "我的数据详情", description = "")
    @PostMapping("/myInfo")
    protected R<T> myInfo( Long id ){
        T byId = service.myDetails(id);
        
        if (ObjectUtil.isEmpty( byId )){
            return R.error( "找不到数据"  );
        }

        return R.ok(byId);
    }
    
    

    
    
}
