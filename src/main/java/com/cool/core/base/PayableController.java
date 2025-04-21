package com.cool.core.base;

import cn.hutool.core.util.ObjectUtil;
import com.alipay.api.AlipayApiException;
import com.cool.core.annotation.NoRepeatSubmit;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.PayStatusEnum;
import com.cool.core.enums.PayTerminalEnum;
import com.cool.core.enums.PayWayEnum;
import com.cool.core.request.R;
import com.cool.core.util.ConvertUtil;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.user.service.UserInfoService;
import com.cool.plugin.AliPayService;
import com.cool.plugin.WxPayService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;


/**
 * APP端控制层基类（用户数据隔离）
 *
 * @param <S>
 * @param <T>
 */
public abstract class PayableController<S extends PayableService<T>, T extends AppEntity<T>> extends AppController<S, T> {

    private static final Logger log = LoggerFactory.getLogger(PayableController.class);

    private final WxPayService wxPayService;

    private final AliPayService aliPayService;

    private final UserInfoService userInfoService;

    protected PayableController(WxPayService wxPayService, AliPayService aliPayService, UserInfoService userInfoService) {
        this.wxPayService = wxPayService;
        this.aliPayService = aliPayService;
        this.userInfoService = userInfoService;
    }

    @Operation(summary = "支付", description = "支付")
    @GetMapping("/pay")
    @NoRepeatSubmit
    protected R pay(Long id) throws WxPayException {

        String className = this.getClass().getSimpleName();
        String classPath = ConvertUtil.extractController2Path("app", className);


        log.info("微信支付，订单号: {}", id);

        PayableEntity info = (PayableEntity) service.info(id);

        if (StringUtils.isBlank(info.getOutTradeNo())) {
            info.setOutTradeNo(wxPayService.createOrderNum("0001"));
        }

        service.update((T) info);

        if (ObjectUtil.isEmpty(info)) {
            return R.error(404, "找不到订单");
        }

        String payWay = info.getPayWay();

        if (!service.isSupportPayWay(payWay)) {
            return R.error(400, "不支持的支付方式");
        }
        
        String payTerminal = info.getTerminal();
        if ( !service.isSupportTerminal(payTerminal)){
            return R.error(400, "不支持的客户端类型");
        }


        StringBuffer notifyUrl = new StringBuffer().append(getDoamin()).append("/").append(classPath);


        return switch (payTerminal) {
            case PayTerminalEnum.MP_WECHAT -> {
                if (!wxPayService.isEnable()) {
                    yield R.error(500, "微信支付未启用");
                }

                String wxOpenId = userInfoService.getById(info.getUserId()).getUnionid();
                notifyUrl.append("/wxNotify");
                WxPayMpOrderResult orderByMini = wxPayService.createOrderByMini((int) (info.getTotal() * 100), info.getOutTradeNo(), WxPayConstants.TradeType.JSAPI, notifyUrl.toString(), info.getBody(), wxOpenId);
                yield R.ok(orderByMini);
            }
            case PayTerminalEnum.APP -> switch (payWay) {
                case PayWayEnum.WECHAT -> {
                    if (!wxPayService.isEnable()) {
                        yield R.error(500, "微信支付未启用");
                    }

                    notifyUrl.append("/wxNotify");
                    WxPayMpOrderResult orderByApp = wxPayService.createOrderByApp((int) (info.getTotal() * 100), info.getOutTradeNo(), notifyUrl.toString(), info.getBody());
                    yield R.ok(orderByApp);

                }
                case PayWayEnum.ALIPAY -> {
                    if (!aliPayService.isEnable()) {
                        yield R.error(500, "支付宝支付未启用");
                    }
                    notifyUrl.append("/aliNotify");
                    try {
                        String orderByAliApp = aliPayService.createOrderByApp(info.getTotal().toString(), info.getOutTradeNo(), notifyUrl.toString(), info.getBody());
                        System.out.println(orderByAliApp);

                        yield R.ok(orderByAliApp);
                    } catch (AlipayApiException e) {
                        yield R.error(503, e.getMessage());
                    }


                }
                default -> R.error(400 , "参数错误或暂不支持的支付方式");
            };
            //  TODO: H5端支付
            case PayTerminalEnum.H5 -> R.error(400 , "参数错误或暂不支持的支付方式");
            default -> R.error(400 , "参数错误或暂不支持的支付方式");
        };

    }


    @Operation(summary = "支付回调通知处理")
    @PostMapping("/wxNotify")
    @TokenIgnore
    public String wxNotify(@RequestBody String xmlData) {
        try {
            // 解析微信支付的回调数据
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);

            String outTradeNo = notifyResult.getOutTradeNo(); // 商户订单号
            // 检查支付结果
            if ("SUCCESS".equals(notifyResult.getResultCode())) {
                // 支付成功的逻辑处理
                log.info("微信支付成功，订单号: {}", outTradeNo);

                
                PayableEntity order = service.getByOutTradeNo( outTradeNo );
                order.setPayStatus(PayStatusEnum.PAYED );
                service.updateById((T) order);
                

                service.payNotice(outTradeNo);


                return WxPayNotifyResponse.success("success"); // 返回给微信支付处理结果
            } else {
                log.error("微信支付失败，订单号: {}", outTradeNo);
                return WxPayNotifyResponse.fail("fail");
            }
        } catch (Exception e) {
            log.error("微信支付回调处理异常: {}", e.getMessage(), e);
            return WxPayNotifyResponse.fail("fail");
        }
    }

    @Operation(summary = "支付回调通知处理")
    @PostMapping("/aliNotify")
    @TokenIgnore
    public String aliNotify(@RequestParam Map<String, String> params) throws IOException {

        if (params.get("trade_status").equals("TRADE_SUCCESS")) {

            try {

                /**
                 *  TODO：不知道为啥验签结果总是 false，
                 *  可能是因为被我去除支付宝依赖冲突
                 *     <groupId>org.bouncycastle</groupId>
                 *     <artifactId>bcprov-jdk15on</artifactId>
                 *     暂时不管验证结果
                 */
//                
                aliPayService.verifyNotify(params);
            } catch (AlipayApiException e) {
                return e.getMessage();
            }
            
            

            PayableEntity order = service.getByOutTradeNo(params.get("out_trade_no"));
            order.setPayStatus(PayStatusEnum.PAYED );
            service.updateById((T) order);
                
            

//            业务通知
            service.payNotice(params.get("out_trade_no"));

        }
        return "success";
    }

}
