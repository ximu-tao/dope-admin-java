package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserBalanceLogEntity;

import java.math.BigDecimal;

/**
 * 用户余额变更记录
 */
public interface UserBalanceLogService extends BaseService<UserBalanceLogEntity> {
    
    /**
     * 余额减少
     * @param userId 用户ID
     * @param amount 减少积分
     * @param remarks 备注
     * @return 是否成功
     */
    Boolean consumption(Long userId, BigDecimal amount , String remarks);
    
    /**
     * 余额增加
     * @param userId 用户ID
     * @param amount 增加积分
     * @param remarks 备注
     * @return 是否成功
     */
    Boolean production(Long userId, BigDecimal amount , String remarks);
}
