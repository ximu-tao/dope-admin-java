package com.cool.core.pay;

import com.cool.core.base.BaseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PayWayService {

    /**
     * 创建订单
     * 返回值会直接返回给客户端
     */
    Object create(PayableEntity entity, Long payerId, String notifyUrl , String returnUrl, PayableService<?> service) throws Exception;

    /**
     * 支付回调,返回值直接作为响应
     * @param request
     * @param httpResponse
     * @param service
     * @throws Exception
     */
    default Object notify( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<?> service) throws Exception{
        
        String outTradeNo = parseOutTradeNo( request , httpResponse , service );

        PayableEntity order = service.getByOutTradeNo(outTradeNo);
        if (verify( request , httpResponse , service, order )) {
            service.payNotice( outTradeNo );
        }
        return "success";
    };

    
    String parseOutTradeNo( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<?> service) throws Exception;
    
    
    
    Boolean verify( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<?> service, PayableEntity entity ) throws Exception;
}
