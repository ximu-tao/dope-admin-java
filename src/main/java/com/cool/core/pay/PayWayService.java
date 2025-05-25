package com.cool.core.pay;

import com.cool.core.base.BaseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PayWayService {

    /**
     * 创建订单
     * 返回值会直接返回给客户端
     */
    <T extends BaseEntity<T> & PayableEntity<T>> Object create(T entity, Long payerId, String notifyUrl , String returnUrl, PayableService<T> service) throws Exception;

    /**
     * 支付回调,返回值直接作为响应
     * @param request
     * @param httpResponse
     * @param service
     * @throws Exception
     */
    default <T extends BaseEntity<T> & PayableEntity<T>>  Object notify( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<T> service) throws Exception{
        
        String outTradeNo = parseOutTradeNo( request , httpResponse , service );

        T order = service.getByOutTradeNo(outTradeNo);
        if (verify( request , httpResponse , service, order )) {
            service.payNotice( outTradeNo );
        }
        return "success";
    };

    
    <T extends BaseEntity<T> & PayableEntity<T>> String parseOutTradeNo( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<T> service) throws Exception;
    
    
    
    <T extends BaseEntity<T> & PayableEntity<T>> Boolean verify( HttpServletRequest request , HttpServletResponse httpResponse , PayableService<T> service, T entity ) throws Exception;
}
