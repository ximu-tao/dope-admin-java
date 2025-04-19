package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserOauthEntity;
import com.cool.modules.user.mapper.UserOauthMapper;
import com.cool.modules.user.service.UserOauthService;
import org.springframework.stereotype.Service;

/**
 * 第三方绑定信息
 */
@Service
public class UserOauthServiceImpl extends BaseServiceImpl<UserOauthMapper, UserOauthEntity> implements UserOauthService {
}