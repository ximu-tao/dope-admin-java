package com.cool.core.base;

public interface PayableService<T> extends BaseService<T> {

    /**
     * 支付完成通知
     * @param outTradeNo 支付单号
     */
    void payNotice( String outTradeNo );

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
    PayableEntity getByOutTradeNo( String outTradeNo );
}
