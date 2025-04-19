package com.cool.modules.user.service.impl;

import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.ModifyEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.user.entity.UserBalanceLogEntity;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.mapper.UserBalanceLogMapper;
import com.cool.modules.user.service.UserBalanceLogService;
import com.cool.modules.user.service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户余额变更记录
 */
@Service
@RequiredArgsConstructor
public class UserBalanceLogServiceImpl extends BaseServiceImpl<UserBalanceLogMapper, UserBalanceLogEntity> implements UserBalanceLogService {

    private final UserSecurityService userSecurityService;


    @Override
    @Transactional
    public Boolean consumption(Long userId, BigDecimal amount, String remarks) {

        UserSecurityEntity byId = userSecurityService.getByUserId(userId);

        CoolPreconditions.check(!(byId.getBalance().compareTo(amount) >= 0), "余额不足");

        UserBalanceLogEntity entity = new UserBalanceLogEntity();
        entity.setUserId(userId);
        entity.setOldBalance(byId.getBalance());
        entity.setBalance(amount);

        byId.setBalance(byId.getBalance().subtract(amount));
        CoolPreconditions.check(!(userSecurityService.update(byId)), "并发错误");

        entity.setNewBalance(byId.getBalance());
        entity.setRemarks(remarks);
        CoolPreconditions.check(!(this.save(entity)), "修改错误");
        return true;
    }

    @Override
    @Transactional
    public Boolean production(Long userId, BigDecimal amount, String remarks) {

        UserSecurityEntity byId = userSecurityService.getByUserId(userId);

        UserBalanceLogEntity entity = new UserBalanceLogEntity();
        entity.setUserId(userId);
        entity.setOldBalance(byId.getBalance());
        entity.setBalance(amount);

        byId.setBalance(byId.getBalance().add(amount));
        CoolPreconditions.check(!(userSecurityService.update(byId)), "并发错误");

        entity.setNewBalance(byId.getBalance());
        entity.setRemarks(remarks);
        CoolPreconditions.check(!(this.save(entity)), "修改错误");
        return true;
    }
}