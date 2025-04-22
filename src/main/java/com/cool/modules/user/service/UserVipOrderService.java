package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.core.pay.PayableService;
import com.cool.modules.user.entity.UserVipOrderEntity;

/**
 * 会员订单
 */
public interface UserVipOrderService extends BaseService<UserVipOrderEntity> , PayableService<UserVipOrderEntity> {
}
