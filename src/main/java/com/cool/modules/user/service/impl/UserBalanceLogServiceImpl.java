package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserBalanceLogEntity;
import com.cool.modules.user.mapper.UserBalanceLogMapper;
import com.cool.modules.user.service.UserBalanceLogService;
import org.springframework.stereotype.Service;

/**
 * 用户余额变更记录
 */
@Service
public class UserBalanceLogServiceImpl extends BaseServiceImpl<UserBalanceLogMapper, UserBalanceLogEntity> implements UserBalanceLogService {
}