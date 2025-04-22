package com.cool.core.base;

import cn.hutool.core.util.RandomUtil;
import com.cool.core.pay.PayableEntity;

public interface BasePaymentService {
    
    Object create(PayableEntity entity, Long payerId, String notifyUrl ) throws Exception;
    
    
    /**
    * 生成订单号，基于时间戳+唯一字符串+随机数+可选的子ID
    *
    * @param subId 可选，如你的订单ID, 或者用户ID的一些组合
    * @return 订单号
    */
    default String createOrderNum( String subId ) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = RandomUtil.randomString(8);
        int randomNumber = 666;
        return timestamp + randomString + randomNumber + (subId != null ? subId : "");
    }
    
}
