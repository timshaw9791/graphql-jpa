package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.service.PayService;
import org.crygier.graphql.wechatpay.model.request.PayRequest;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.service.WeChatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/8/29 9:49
 */

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private OrderService orderService;

    @Override
    public PayResponse weChatPay(String orderId,String ip) {

        Order order = orderService.findOne(orderId);

        PayRequest payRequest = new PayRequest();
        payRequest.setOrderAmount(1L);
        payRequest.setOrderId(order.getId());
        payRequest.setOrderName("猛龙商城");
        payRequest.setSpbillCreateIp(ip);
        payRequest.setSceneInfo("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://menglongchuxing.cn\",\"wap_name\": \"猛龙商城\"}}");

        return weChatPayService.h5pay(payRequest);

    }

}
