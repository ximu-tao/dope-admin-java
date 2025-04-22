package com.cool.core.pay;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;

public interface PayableService<T extends BaseEntity<T> & PayableEntity> extends BaseService<T> {

    /**
     * 支付完成通知
     * @param outTradeNo 支付单号
     */
    void payNotice( String outTradeNo );
    
    
    /**
     * 取消订单
     * 一般仅支持ID参数
     * @param entity
     */
    void close( T entity );

    /**
     * 检查是否支持支付方式
     * @param payWay
     * @return
     */
    Boolean isSupportPayWay( String payWay );
    
    
    /**
     * 检查是否支持客服端类型
     * @param terminal
     * @return
     */
    Boolean isSupportTerminal( String terminal );


    /**
     * 根据订单号获取订单实例
     * @param outTradeNo
     * @return
     */
    T getByOutTradeNo( String outTradeNo );
}
