package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.mapper.UserSecurityMapper;
import com.cool.modules.user.service.UserSecurityService;
import org.springframework.stereotype.Service;

/**
 * 用户重要数据
 */
@Service
public class UserSecurityServiceImpl extends BaseServiceImpl<UserSecurityMapper, UserSecurityEntity> implements UserSecurityService {
}