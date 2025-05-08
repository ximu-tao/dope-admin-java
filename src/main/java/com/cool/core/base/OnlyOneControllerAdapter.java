package com.cool.core.base;

import cn.hutool.json.JSONObject;
import com.cool.core.request.OneParams;
import com.cool.core.request.PageParams;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 每个用户只能有一条的数据，可以继承此接口
 * @param <S>
 * @param <T>
 */
public abstract class OnlyOneControllerAdapter<S extends BaseService<T>, T extends BaseEntity<T> & BelongingUserEntity> extends AppController<S,T> {


    @Operation(summary = "", description = "")
    @PostMapping("/add")
    @Override
    public R<T> add(@Valid @RequestBody T params,@RequestAttribute() JSONObject requestParams) {
        return R.error("禁止使用此接口");
    }

    @Operation(summary = "", description = "")
    @PostMapping("/delete")
    @Override
    protected R<Boolean> delete( @RequestBody T t ,@RequestAttribute() JSONObject requestParams) {
        return R.error("禁止使用此接口");
    }
    
    @Operation(summary = "修改", description = "修改")
    @PostMapping("/update")
    protected R<Boolean> update(@RequestBody T t,@RequestAttribute() JSONObject requestParams) {
        R<T> tr = this.myInfo(new OneParams(), requestParams);
        t.setId( tr.getData().getId() );
        t.setUserId( CoolSecurityUtil.getCurrentUserId() );
        Boolean modify = service.update(t);
        return R.ok(modify);
    }
    

    
    @SneakyThrows
    @Override
    @Operation(summary = "我的数据（不需要ID参数）", description = "")
    @PostMapping("/myInfo")
    protected R<T> myInfo( @RequestBody OneParams oneParams, @RequestAttribute() JSONObject requestParams ){
        
        if ( this.service instanceof OnlyOneService<?> onlyOneService){
            T o = (T) onlyOneService.infoByUserId(CoolSecurityUtil.getCurrentUserId(), oneParams.getWith());
            return R.ok(o);
        }
        
        T t = currentEntityClass().getDeclaredConstructor().newInstance();
        t.setUserId( CoolSecurityUtil.getCurrentUserId() );

        Page<T> tPage = this.service.pageWithRelationsForUser(
                requestParams,
                new Page<T>(1, 1),
                QueryWrapper.create(t), 
                oneParams.getWith(),
                CoolSecurityUtil.getCurrentUserId()
        );

        if ( tPage.getTotalPage() == 0 ){
            service.add( t );
            return R.ok( t );
        }else {
            return R.ok( tPage.getRecords().get(0) );
        }
    }
    
}
