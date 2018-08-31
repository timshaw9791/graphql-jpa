package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.model.response.RefundResponse;

/**
 * @author Curtain
 * @date 2018/8/29 9:43
 */
public interface PayService {

    /**
     * wap h5 支付   分为ios的 和 android的  还有pc wap的
     * @param orderId
     * @param ip
     * @return
     */
    PayResponse weChatPay(String orderId, String ip);

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
