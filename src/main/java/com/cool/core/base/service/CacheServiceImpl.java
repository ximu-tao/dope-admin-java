package com.cool.core.base.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.BelongingUserEntity;
import com.cool.core.util.RedisUtils;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

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

        T info = super.getById( id );
        RedisUtils.setCacheObject( cacheKey , info);
        return info;
    }


    @Override
    public Page<T> pageWithRelations(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with) {
        
        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelations(requestParams, page, queryWrapper, with);
        }

        String simpleName = this.getClass().getSimpleName();
        String cacheName = simpleName + "::page::" + page.toString() + "::" + queryWrapper.toSQL();
        return gettPage(requestParams, (Page<T>) page, queryWrapper, cacheName);

    }


    @Override
    public Page<T> pageWithRelationsForUser(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with, Long userId) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelationsForUser(requestParams, page, queryWrapper, with, userId);
        }

        String simpleName = this.getClass().getSimpleName();
        String cacheName = simpleName + "::userid::" + userId + "::page::" + page.toString() + "::" + queryWrapper.toSQL();
        return gettPage(requestParams, (Page<T>) page, queryWrapper, cacheName);

    }

    private Page<T> gettPage(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, String cacheName) {
        Page<T> cacheObject = RedisUtils.getCacheObject(cacheName);
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            log.info("缓存命中：" + cacheName );
            return cacheObject;
        }

        Page<T> tPage = super.page(requestParams, page, queryWrapper );

        RedisUtils.setCacheObject(cacheName, tPage);

        return tPage;

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
