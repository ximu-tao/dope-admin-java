package com.cool.core.base.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.lock.CoolLock;
import com.cool.core.util.RedisUtils;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Arrays;

import java.util.List;


/**
 * 通用的缓存 Service
 *
 * @param <M>
 * @param <T>
 */
@Slf4j
public class CacheServiceImpl<M extends BaseMapper<T>, T extends BaseEntity<T>> extends BaseServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private CoolLock coolLock;

    @Override
    public T info(Long id, List<String> with) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.info(id, with);
        }

        String simpleName = this.getClass().getSimpleName();

        String cacheKey = simpleName + "::info::" + id;
        T cacheObject = RedisUtils.getCacheObject( cacheKey );
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            log.info("缓存命中：" + cacheKey );
            return cacheObject;
        }


        if (coolLock.tryLock(cacheKey, Duration.ofMillis( 2000 ) )) {

            T tryCacheObject = RedisUtils.getCacheObject( cacheKey );
            if (  !ObjectUtil.isNotEmpty( tryCacheObject )) {
                T info = super.getById( id );
                RedisUtils.setCacheObject( cacheKey , info);
                cacheObject = info;
            }
            
            coolLock.unlock( cacheKey );
        }
        

        return cacheObject;
    }


    @Override
    public Page<T> pageWithRelations(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with) {
        
        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelations(requestParams, page, queryWrapper, with);
        }

        String simpleName = this.getClass().getSimpleName();
        String cacheKey = simpleName + "::page::" + page.toString() + "::" + queryWrapper.toSQL();
        return gettPage(requestParams, (Page<T>) page, queryWrapper, cacheKey);

    }


    @Override
    public Page<T> pageWithRelationsForUser(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with, Long userId) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelationsForUser(requestParams, page, queryWrapper, with, userId);
        }

        String simpleName = this.getClass().getSimpleName();
        String cacheKey = simpleName + "::userid::" + userId + "::page::" + page.toString() + "::" + queryWrapper.toSQL();
        return gettPage(requestParams, (Page<T>) page, queryWrapper, cacheKey);

    }

    private Page<T> gettPage(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, String cacheKey) {
        Page<T> cacheObject = RedisUtils.getCacheObject(cacheKey);
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            log.info("缓存命中：" + cacheKey );
            return cacheObject;
        }
        
        
        
        if (coolLock.tryLock(cacheKey, Duration.ofMillis( 2000 ) )) {

            T tryCacheObject = RedisUtils.getCacheObject( cacheKey );
            if (  !ObjectUtil.isNotEmpty( tryCacheObject )) {
                
                Page<T> tPage = super.page(requestParams, page, queryWrapper );
                
                
                 RedisUtils.setCacheObject(cacheKey, tPage);
                 
                 cacheObject = tPage;
            }
            
            coolLock.unlock( cacheKey );
        }
        
        return cacheObject;

    }
    
    protected void clearUserCache( T entity ){
        if (ObjectUtil.isEmpty(entity)) {
            return;
        }
        String simpleName = this.getClass().getSimpleName();
        if ( entity instanceof BelongingUserEntity userEntity) {
                RedisUtils.deleteKeys(simpleName + "::userid::" + userEntity.getUserId() + "::page::*" );
        }
    }

    @Override
    public boolean update(T entity) {
        
        boolean update = super.update(entity);

        if (update) {
            String simpleName = this.getClass().getSimpleName();
            RedisUtils.setCacheObject(simpleName + "::info::" + entity.getId(), entity);
            RedisUtils.deleteKeys(simpleName + "::page::*");
            
            this.clearUserCache( entity );
            
        }

        return update;
    }


    @Override
    public Long add(T entity) {
        Long add = super.add(entity);

        if (add != null && add > 0) {
            String simpleName = this.getClass().getSimpleName();
            RedisUtils.setCacheObject(simpleName + "::info::" + entity.getId(), entity);
            RedisUtils.deleteKeys(simpleName + "::page::*");
            
            this.clearUserCache( entity );
            
        }

        return add;
    }


    @Override
    public boolean delete(Long... ids) {
        
        String simpleName = this.getClass().getSimpleName();
        if ( ids.length == 1 ){
            T info = this.info(ids[0], null);
            this.clearUserCache( info );
            RedisUtils.deleteKeys(simpleName + "::page::*");
        }else if ( ids.length < 5 ){
            for ( Long id : ids ){
                List<T> ts = this.listByIds(Arrays.asList(ids));
                ts.forEach( this::clearUserCache );
            }
            RedisUtils.deleteKeys(simpleName + "::page::*");
        }else {
            RedisUtils.deleteKeys(simpleName + "::*");
        }
        
        
        boolean delete = super.delete(ids);

        if (delete) {

            Arrays.stream(ids).forEach(id -> {
                RedisUtils.deleteObject(simpleName + "::info::" + id);
            });
        }

        return delete;
    }
}
