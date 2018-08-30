package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.wechatpay.model.response.PayResponse;

/**
 * @author Curtain
 * @date 2018/8/29 9:43
 */
public interface PayService {

    PayResponse weChatPay(String orderId, String ip);

}
