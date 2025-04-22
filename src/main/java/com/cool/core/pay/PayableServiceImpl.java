package com.cool.core.pay;

import com.cool.core.base.*;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;


public class PayableServiceImpl<M extends BaseMapper<T>, T extends BaseEntity<T> & PayableEntity> extends BaseServiceImpl<M, T> implements BaseService<T>, PayableService<T> {


    @Override
    public void payNotice(String outTradeNo) {
        
    }

    @Override
    public void close(T entity) {

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
            case PayTerminalEnum.APP, PayTerminalEnum.H5, PayTerminalEnum.MP_WECHAT, PayTerminalEnum.WOA, PayTerminalEnum.PC:
                return true;
            default:
                return false;
        }
    }

    @Override
    public T getByOutTradeNo(String outTradeNo) {

        QueryWrapper qw = QueryWrapper.create().eq( T::getOutTradeNo , outTradeNo);

        return this.mapper.selectOneByQuery(qw);
    }
}
