package org.crygier.graphql.mlshop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.service.PayService;
import org.crygier.graphql.wechatpay.model.request.PayRequest;
import org.crygier.graphql.wechatpay.model.request.RefundRequest;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.model.response.RefundResponse;
import org.crygier.graphql.wechatpay.service.WeChatPayService;
import org.crygier.graphql.wechatpay.utils.IPAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Curtain
 * @date 2018/8/29 9:49
 */

@Service
@Slf4j
public class PayServiceImpl implements PayService {
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private OrderService orderService;

    @Override
    public PayResponse notify(String notifyData) {
        PayResponse payResponse = weChatPayService.asyncNotify(notifyData);

        log.info("微信支付异步回调" + payResponse);
        Order order = orderService.findOne(payResponse.getOrderId());


        if (!(order.getFrontMoney() - (payResponse.getOrderAmount()) == 0)) {
            throw new RuntimeException("金额不正确");
        }

        //订单支付
        orderService.paid(order.getId());

        return payResponse;
    }

    @Override
    public RefundResponse refund(String orderId) {
        RefundRequest refundRequest = new RefundRequest();

        Order order = orderService.findOne(orderId);

        refundRequest.setOrderId(orderId);
        refundRequest.setOrderAmount(order.getFrontMoney());

        RefundResponse refundResponse = weChatPayService.refund(refundRequest);

        orderService.refund(orderId);

        return refundResponse;
    }

    @Override
    public PayResponse weChatPay(String orderId, HttpServletRequest httpServletRequest) {

//        Order order = orderService.findOne(orderId);

//        PayRequest payRequest = new PayRequest();
//        payRequest.setOrderAmount(1L);
//        payRequest.setOrderId(order.getId());
//        payRequest.setOrderName("猛龙商城");
//        payRequest.setSpbillCreateIp(IPAddressUtil.getIPAddress(httpServletRequest));
//        payRequest.setSceneInfo("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://menglongchuxing.cn\",\"wap_name\": \"猛龙商城\"}}");

        PayRequest payRequest = new PayRequest();
        payRequest.setOrderAmount(1L);
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("猛龙商城");
        payRequest.setSpbillCreateIp(IPAddressUtil.getIPAddress(httpServletRequest));
        payRequest.setSceneInfo("{\"h5_info\": {\"type\":\"Android\",\"app_name\": \"猛龙商城\",\"package_name\": \"com.raptorsTravel.raptorsMal\"}}");

        return weChatPayService.h5pay(payRequest);

    }

}
