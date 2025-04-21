package com.cool.core.base;

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
     * 支付渠道
     * @return
     */
    public String getPayWay();

    /**
     * 支付总价
     * @return
     */
    public Double getTotal();
    
}
