package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.model.response.RefundResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Curtain
 * @date 2018/8/29 9:43
 */
public interface PayService {

    /**
     * wap h5 支付   分为ios的 和 android的  还有pc wap的
     * @param orderId
     * @return
     */
    PayResponse weChatPay(String orderId,HttpServletRequest httpServletRequest);

    /**
     * 微信回调
     * @param notifyData
     * @return
     */
    PayResponse notify(String notifyData);

    /**
     * 异步通知回调
     * @param orderId
     * @return
     */
    RefundResponse refund(String orderId);




}
