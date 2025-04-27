package com.cool.core.base;

import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

public interface OnlyOneService<T extends BaseEntity<T> & BelongingUserEntity> extends BaseService<T> {

    /**
     * 根据用户ID查询一条数据
     *
     * @param userId
     * @return
     */
    default T infoByUserId(Long userId, List<String> with) {
        try {
            Class<T> entityClass = currentEntityClass();

            T t = entityClass.getDeclaredConstructor().newInstance();
            t.setUserId(userId);
            T one = this.getOne(QueryWrapper.create(t));

            if (one == null) {
                this.add(t);
                return t;
            }

            return one;

        } catch (Exception e) {
            return null;
        }
    }

}
