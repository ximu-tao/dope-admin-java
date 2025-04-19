package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserPointsLogEntity;
import com.cool.modules.user.mapper.UserPointsLogMapper;
import com.cool.modules.user.service.UserPointsLogService;
import org.springframework.stereotype.Service;

/**
 * 用户积分变更记录
 */
@Service
public class UserPointsLogServiceImpl extends BaseServiceImpl<UserPointsLogMapper, UserPointsLogEntity> implements UserPointsLogService {
}