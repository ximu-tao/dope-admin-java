package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.mapper.UserSecurityMapper;
import com.cool.modules.user.service.UserSecurityService;
import org.springframework.stereotype.Service;

/**
 * 用户重要数据
 */
@Service
public class UserSecurityServiceImpl extends BaseServiceImpl<UserSecurityMapper, UserSecurityEntity> implements UserSecurityService {
    
    @Override
    public UserSecurityEntity getByUserId(Long userId) {
        UserSecurityEntity byId = this.getById(userId);

        if (byId == null) {
            byId = new UserSecurityEntity();
            byId.setUserId(userId);
            byId.setId(userId);
            byId.save();
        }

        if (byId.getUserId() != userId) {
            CoolPreconditions.alwaysThrow("用户重要数据异常", byId);
        }

        return byId;
    }

}