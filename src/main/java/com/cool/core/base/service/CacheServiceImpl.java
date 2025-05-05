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
import java.util.function.Supplier;


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

    private static final Duration LOCK_TIMEOUT = Duration.ofMillis(2000);

    private String simpleName = null;

    protected String getSimpleName() {
        if (simpleName != null) {
            return simpleName;
        }
        return this.simpleName = this.getClass().getSimpleName();
    }

    protected String generateCacheKey( String... parts) {
        StringBuilder key = new StringBuilder(this.getSimpleName());
        for (Object part : parts) {
            key.append("::").append(part);
        }
        return key.toString();
    }
    
    protected <R> R getOrSetCache(String cacheKey, Supplier<R> supplier) {
        R cacheObject = RedisUtils.getCacheObject(cacheKey);
        if (ObjectUtil.isNotNull(cacheObject)) {
            log.debug("cache hit : {}", cacheKey);
            return cacheObject;
        }
        log.debug("cache not hit : {}", cacheKey);

        if (coolLock.tryLock(cacheKey, LOCK_TIMEOUT)) {
            try {
                cacheObject = RedisUtils.getCacheObject(cacheKey);
                if (ObjectUtil.isEmpty(cacheObject)) {
                    
                    cacheObject = supplier.get();
                    
                    RedisUtils.setCacheObject(cacheKey, cacheObject);
                }
            } finally {
                coolLock.unlock(cacheKey);
            }
        }
        return cacheObject;
    }

    @Override
    public T info(Long id, List<String> with) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.info(id, with);
        }
        
        String cacheKey = generateCacheKey( "info", id.toString());

        return this.getOrSetCache(cacheKey, () -> super.getById(id));
    }

    @Override
    public Page<T> page( Page<T> page, QueryWrapper queryWrapper) {
        String cacheKey = generateCacheKey( "page", page.toString(), "query", queryWrapper.toSQL());
        return this.getOrSetCache(cacheKey, () -> super.page( page, queryWrapper));
    }

    @Override
    public List<T> list( QueryWrapper queryWrapper) {
        String cacheKey = generateCacheKey( "list", queryWrapper.toSQL());
        return this.getOrSetCache(cacheKey, () -> super.list( queryWrapper));
    }

    @Override
    public Page<T> pageWithRelations(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelations(requestParams, page, queryWrapper, with);
        }

        return this.page(requestParams, page, queryWrapper);

    }


    @Override
    public Page<T> pageWithRelationsForUser(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper, List<String> with, Long userId) {

        if (ObjectUtil.isNotEmpty(with)) {
//            有关联数据，涉及第三方表 不缓存
            return super.pageWithRelationsForUser(requestParams, page, queryWrapper, with, userId);
        }

        String cacheKey = generateCacheKey("userid", userId.toString(), "page", page.toString(), "query", queryWrapper.toSQL());
        
        return getOrSetCache( cacheKey, () -> super.page( page, queryWrapper) );

    }

    protected void clearUserCache(T entity) {
        if (ObjectUtil.isEmpty(entity)) {
            return;
        }
        if (entity instanceof BelongingUserEntity userEntity) {
            RedisUtils.deleteKeys( generateCacheKey( "userid", userEntity.getUserId().toString(), "page", "*" ) );
        }
    }
    
    protected void updateCache(T entity) {
        RedisUtils.deleteKeys(generateCacheKey( "page", "*" ) );
        RedisUtils.deleteKeys(generateCacheKey( "list", "*" ) );
        if (ObjectUtil.isEmpty(entity)) {
            return;
        }
        RedisUtils.setCacheObject( generateCacheKey( "info", entity.getId().toString()) , entity);
        this.clearUserCache(entity);
    }

    @Override
    public boolean update(T entity) {

        boolean update = super.update(entity);

        if (update) {
            this.updateCache(entity);

        }

        return update;
    }


    @Override
    public Long add(T entity) {
        Long add = super.add(entity);

        if (add != null && add > 0) {

            this.updateCache(entity);

        }

        return add;
    }


    @Override
    public boolean delete(Long... ids) {

        if (ids.length == 1) {
            T info = this.info(ids[0], null);
            this.clearUserCache(info);
        } else if (ids.length < 5) {
            List<T> ts = this.listByIds(Arrays.asList(ids));
            ts.forEach(this::clearUserCache);
            
        }
        
        this.updateCache(null);


        boolean delete = super.delete(ids);

        if (delete) {

            Arrays.stream(ids).forEach(id -> {
                RedisUtils.deleteObject(generateCacheKey("info", id.toString()) );
            });
        }

        return delete;
    }
}
