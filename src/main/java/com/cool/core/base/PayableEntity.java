package com.cool.core.base;

import java.time.LocalDateTime;

public interface PayableEntity extends BelongingUserEntity{
    
    /**
     * 设置支付状态
     * @param payStatus
     */
    public void setPayStatus(Integer payStatus);

    /**
     * 获取支付状态
     * @return
     */
    public Integer getPayStatus();
    
    
    /**
     * 订单号
     * @return
     */
    public void setOutTradeNo( String outTradeNo );
    
    /**
     * 订单号
     * @return
     */
    public String getOutTradeNo();

    /**
     * 商品描述
     * @return
     */
    public String getBody();

    /**
     * 客户端类型
     * @return
     */
    public String getTerminal();

    /**
     * 修改客户端类型
     * @param terminal
     */
    public void setTerminal( String terminal );
    
    /**
     * 支付渠道
     * @return
     */
    public String getPayWay();

    /**
     * 修改支付渠道
     * @param payWay
     */
    void setPayWay( String payWay );
    /**
     * 支付总价
     * @return
     */
    public Double getTotal();
    
    
    /**
     * 设置支付时间
     */
    public void setPayTime( LocalDateTime payTime );
}
