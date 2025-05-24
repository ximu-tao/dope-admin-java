package com.cool.core.pay;

import cn.hutool.core.util.IdUtil;

public interface BasePaymentService extends PayWayService{
    
    /**
    * 生成订单号，基于时间戳+唯一字符串+随机数+可选的子ID
    *
    * @param subId 可选，如你的订单ID, 或者用户ID的一些组合
    * @return 订单号
    */
    static String createOrderNum( String subId ) {
        return subId+IdUtil.getSnowflake().nextIdStr();
    }
    
}
