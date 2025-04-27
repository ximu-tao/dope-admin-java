package com.cool.core.base.service;

import cn.hutool.core.lang.func.Supplier4;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.cache.CoolCache;
import com.cool.core.util.RedisUtils;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.Arrays;

import java.util.List;


/**
 * 通用的缓存 Service
 *
 * @param <M>
 * @param <T>
 */
public class CacheService<M extends BaseMapper<T>, T extends BaseEntity<T>> extends BaseServiceImpl<M, T> implements BaseService<T> {


    @Override
    public T info(Long id, List<String> with) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.info(id, with);
        }

        String simpleName = this.getClass().getSimpleName();

        T cacheObject = RedisUtils.getCacheObject(simpleName + "::info::" + id);
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            return cacheObject;
        }

        T info = super.getById( id );
        RedisUtils.setCacheObject(simpleName + "::info::" + id, info);
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
        Page<T> cacheObject = RedisUtils.getCacheObject(cacheName);
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            return cacheObject;
        }

        Page<T> tPage = super.page(requestParams, page, queryWrapper );

        RedisUtils.setCacheObject(cacheName, tPage);

        return tPage;

    }


    @Override
    public boolean update(T entity) {
        
        boolean update = super.update(entity);

        if (update) {
            String simpleName = this.getClass().getSimpleName();
            RedisUtils.setCacheObject(simpleName + "::info::" + entity.getId(), entity);
            RedisUtils.deleteKeys(simpleName + "::page::*");
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
        }

        return add;
    }


    @Override
    public boolean delete(Long... ids) {
        boolean delete = super.delete(ids);

        if (delete) {

            String simpleName = this.getClass().getSimpleName();
            Arrays.stream(ids).forEach(id -> {
                RedisUtils.deleteObject(simpleName + "::info::" + id);
            });
            RedisUtils.deleteKeys(simpleName + "::page::*");
        }

        return delete;
    }
}
