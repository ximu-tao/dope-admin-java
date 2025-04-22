package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.modules.user.entity.UserSecurityEntity;
import com.cool.modules.user.entity.UserVipInfoEntity;
import com.cool.modules.user.entity.UserVipOrderEntity;
import com.cool.modules.user.entity.table.UserVipOrderEntityTableDef;
import com.cool.modules.user.mapper.UserVipOrderMapper;
import com.cool.modules.user.service.UserSecurityService;
import com.cool.modules.user.service.UserVipInfoService;
import com.cool.modules.user.service.UserVipOrderService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 会员订单
 */
@Service
public class UserVipOrderServiceImpl extends BaseServiceImpl<UserVipOrderMapper, UserVipOrderEntity> implements UserVipOrderService {


    private final UserVipInfoService userVipInfoService;
    private final UserSecurityService userSecurityService;

    public UserVipOrderServiceImpl(UserVipInfoService userVipInfoService, UserSecurityService userSecurityService) {
        super();
        this.userVipInfoService = userVipInfoService;
        this.userSecurityService = userSecurityService;
    }

    @Override
    public Long create(UserVipOrderEntity entity) {
        
        Long vipId = entity.getVipId();
        UserVipInfoEntity vip = userVipInfoService.getById(vipId);

        entity.setTitle(vip.getTitle());
        entity.setPrice( vip.getPrice() );
        entity.setDay(vip.getDay());
        entity.setPayStatus(PayStatusEnum.PAYING);

        return super.create(entity);
    }
    
    
    
    
    
    
    
    @Override
    public void payNotice(String outTradeNo) {
        UserVipOrderEntity order = getByOutTradeNo(outTradeNo);
        
        Long userId = order.getUserId();
        UserSecurityEntity user = userSecurityService.getByUserId(userId);
        
        Integer day = order.getDay();
        
        Date vipTime = user.getVipTime();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vipTime);
        calendar.add(Calendar.DAY_OF_YEAR, day);

        Date newVipTime = calendar.getTime();
        
        user.setVipTime(newVipTime);

        userSecurityService.updateById(user);

    }

    @Override
    public void close(UserVipOrderEntity entity) {
        this.delete(entity);
    }

    @Override
    public Boolean isSupportPayWay(String payWay) {
        switch (payWay) {
            case PayWayEnum.ALIPAY, PayWayEnum.WECHAT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Boolean isSupportTerminal(String terminal) {
        switch (terminal) {
            case PayTerminalEnum.APP, PayTerminalEnum.H5, PayTerminalEnum.MP_WECHAT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public UserVipOrderEntity getByOutTradeNo(String outTradeNo) {

        QueryWrapper qw = QueryWrapper.create()
                .and(UserVipOrderEntityTableDef.USER_VIP_ORDER_ENTITY.OUT_TRADE_NO.eq(outTradeNo));
        
        return this.mapper.selectOneByQuery(qw);
    }


}