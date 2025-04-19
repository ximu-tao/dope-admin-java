package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserPointsLogEntity;

/**
 * 用户积分变更记录
 */
public interface UserPointsLogService extends BaseService<UserPointsLogEntity> {
    
    /**
     * 积分减少
     * @param userId 用户ID
     * @param points 减少积分
     * @param remarks 备注
     * @return 是否成功
     */
    Boolean consumption( Long userId, Integer points , String remarks);

    /**
     * 积分增加
     * @param userId 用户ID
     * @param points 增加积分
     * @param remarks 备注
     * @return 是否成功
     */
    Boolean production( Long userId, Integer points , String remarks);
}
