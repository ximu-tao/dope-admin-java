package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserVipOrderEntity;
import com.cool.modules.user.mapper.UserVipOrderMapper;
import com.cool.modules.user.service.UserVipOrderService;
import org.springframework.stereotype.Service;

/**
 * 会员订单
 */
@Service
public class UserVipOrderServiceImpl extends BaseServiceImpl<UserVipOrderMapper, UserVipOrderEntity> implements UserVipOrderService {
}