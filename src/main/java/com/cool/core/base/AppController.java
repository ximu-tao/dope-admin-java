package com.cool.core.base;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.OneParams;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;


public abstract class AppController <S extends BaseService<T>, T extends BaseEntity<T>> extends BaseController<S,T> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        
    }
    
    
    
//    add  delete   update  info   list  page   lists   details
    
    @Operation(summary = "发布/新增/创建", description = "")
    @PostMapping("/add")
    protected R<T> add(@Valid @RequestBody T params,@RequestAttribute() JSONObject requestParams) {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        
        if (params instanceof BelongingUserEntity appEntity){
            appEntity.setUserId(userId);
        }
        
        Long add = service.add( requestParams, params);
        return R.ok(params);
    }

    @Operation(summary = "删除", description = "默认仅支持ID删除，具体看子类实现")
    @PostMapping("/delete")
    protected R<Boolean> delete( @RequestBody T t ,@RequestAttribute() JSONObject requestParams) {
        
        if ( t.getId() == null || t.getId() <= 0 ) {
             CoolPreconditions.alwaysThrow("ID不能为空");
        }
        
        if (t instanceof BelongingUserEntity appEntity){

            BelongingUserEntity byId = (BelongingUserEntity)service.info(t.getId(), null);

            if ( !byId.getUserId().equals( CoolSecurityUtil.getCurrentUserId() )  ){
                return R.error("不是你的数据");
            }
        }
        Boolean delete = service.delete( requestParams , t.getId() );
        return R.ok(delete);
    }
    
    @Operation(summary = "修改", description = "修改")
    @PostMapping("/update")
    protected R<Boolean> update(@RequestBody T t,@RequestAttribute() JSONObject requestParams) {
        
        if ( t.getId() == null || t.getId() <= 0 ) {
             CoolPreconditions.alwaysThrow("ID不能为空");
        }
        
        if (t instanceof BelongingUserEntity ){
            BelongingUserEntity byId = (BelongingUserEntity)service.info( t.getId() , null);

            if ( !byId.getUserId().equals( CoolSecurityUtil.getCurrentUserId() )  ){
                return R.error("不是你的数据");
            }

            ((BelongingUserEntity)t).setUserId( CoolSecurityUtil.getCurrentUserId() );
        }
        
        Boolean modify = service.update( requestParams , t);
        return R.ok(modify);
    }
    
    
    @TokenIgnore
    @Operation(summary = "分页查询数据", description = "")
    @PostMapping("/list")
    protected R<PageResult<T>> list( @Valid @RequestBody PageParams<T> pageParams, @RequestAttribute() JSONObject requestParams ) {
        
        Page<T> TPage = this.service.pageWithRelations( requestParams, pageParams.toPage(), service.buildAppQueryWrapper(pageParams), pageParams.getWith() );

        return R.ok(pageResult(TPage));
    }
    
    
    @Operation(summary = "分页查询我的数据", description = "")
    @PostMapping("/myList")
    protected R<PageResult<T>> myList( @Valid @RequestBody PageParams<T> pageParams, @RequestAttribute() JSONObject requestParams ) {
        
        T params = pageParams.getParams();
        if (params instanceof BelongingUserEntity appEntity){
            appEntity.setUserId(CoolSecurityUtil.getCurrentUserId());
        }
        
        return this.list( pageParams, requestParams );
    }
    
    @TokenIgnore
    @Operation(summary = "数据详情", description = "")
    @PostMapping("/info")
    protected R<T> info(@Valid @RequestBody OneParams oneParams, @RequestAttribute() JSONObject requestParams){

        T byId = service.info( oneParams.getId(), oneParams.getWith() );
        
        if (ObjectUtil.isEmpty( byId )){
            return R.error( "找不到数据"  );
        }

        return R.ok(byId);
    }
    

    @Operation(summary = "我的数据详情", description = "")
    @PostMapping("/myInfo")
    protected R<T> myInfo(@Valid @RequestBody OneParams oneParams, @RequestAttribute() JSONObject requestParams ){
        R<T> r = this.info( oneParams , requestParams );
        
        T byId = r.getData();
        
        if (ObjectUtil.isEmpty( byId )){
            return R.error( "找不到数据"  );
        }
        
        if (byId instanceof BelongingUserEntity appEntity){
            
            if ( !appEntity.getUserId().equals( CoolSecurityUtil.getCurrentUserId() )  ){
                return R.error("不是你的数据");
            }
        }

        return r;
    }
    
    

    
    
}
