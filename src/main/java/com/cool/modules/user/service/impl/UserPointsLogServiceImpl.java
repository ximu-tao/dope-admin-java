package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.entity.UserPointsLogEntity;
import com.cool.modules.user.mapper.UserPointsLogMapper;

import com.cool.modules.user.service.UserPointsLogService;
import com.cool.modules.user.service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户积分变更记录
 */
@Service
@RequiredArgsConstructor
public class UserPointsLogServiceImpl extends BaseServiceImpl<UserPointsLogMapper, UserPointsLogEntity> implements UserPointsLogService {
    private final UserSecurityService userSecurityService;
    
    @Override
    @Transactional 
    public Boolean consumption(Long userId, Integer point , String remarks ) {

        UserSecurityEntity byId = userSecurityService.getByUserId(userId);
        
        CoolPreconditions.check( !(byId.getPoints().compareTo( point ) > 0) , "积分不足");
        
        UserPointsLogEntity entity = new UserPointsLogEntity();
        entity.setUserId(userId);
        entity.setOldPoints( byId.getPoints() );
        entity.setPoints( point);
        
        byId.setPoints(byId.getPoints() - point);

        CoolPreconditions.check( !(userSecurityService.update(byId)), "并发错误");
        
        entity.setNewPoints( byId.getPoints() );
        entity.setRemarks( remarks );
        
        CoolPreconditions.check( !(this.save(entity)) ,  "修改错误" );
        return true;
    }

    @Override
    @Transactional 
    public Boolean production(Long userId, Integer point, String remarks) {
        
        UserSecurityEntity byId = userSecurityService.getById(userId);

        UserPointsLogEntity entity = new UserPointsLogEntity();
        entity.setUserId(userId);
        entity.setOldPoints( byId.getPoints() );
        entity.setPoints( point);
        
        byId.setPoints(byId.getPoints() + point);

         CoolPreconditions.check( !(userSecurityService.update(byId) ) ,  "并发错误" );
        
        entity.setNewPoints( byId.getPoints() );
        entity.setRemarks( remarks );
        
        CoolPreconditions.check(!( this.save(entity)) ,  "修改错误" );
        return true;
        
    }
}