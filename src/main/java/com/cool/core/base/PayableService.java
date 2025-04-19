package com.cool.core.base;

public interface PayableService<T> extends BaseService<T> {

    /**
     * 支付完成通知
     * @param outTradeNo 支付单号
     */
    void payNotice( String outTradeNo );

    /**
     * 检查是否支持支付方式
     * @param payType
     * @return
     */
    Boolean supportPayType( String payType );
}
