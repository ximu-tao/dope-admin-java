package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.core.base.OnlyOneService;
import com.cool.modules.user.entity.UserSecurityEntity;

/**
 * 用户重要数据
 */
public interface UserSecurityService extends BaseService<UserSecurityEntity>, OnlyOneService<UserSecurityEntity> {
    UserSecurityEntity getByUserId(Long userId);
}
