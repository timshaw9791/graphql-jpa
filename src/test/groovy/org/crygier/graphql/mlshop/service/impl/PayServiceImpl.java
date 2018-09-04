package org.crygier.graphql.mlshop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.enums.OrderPayStatusEnum;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.service.PayService;
import org.crygier.graphql.wechatpay.model.request.PayRequest;
import org.crygier.graphql.wechatpay.model.request.RefundRequest;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.model.response.RefundResponse;
import org.crygier.graphql.wechatpay.service.WeChatPayService;
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

        //修改订单支付状态  todo orderservice 增加paid接口
        order.setPayStatusEnum(OrderPayStatusEnum.PAID);


        return payResponse;
    }

    @Override
    public RefundResponse refund(String orderId) {
        RefundRequest refundRequest = new RefundRequest();

        Order order = orderService.findOne(orderId);

        refundRequest.setOrderId(orderId);
        refundRequest.setOrderAmount(order.getFrontMoney());
//        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);

        RefundResponse refundResponse = weChatPayService.refund(refundRequest);

        //todo 修改订单 为退款

        return refundResponse;
    }

    @Override
    public PayResponse weChatPay(String orderId, HttpServletRequest httpServletRequest) {

//        Order order = orderService.findOne(orderId);
//
//        PayRequest payRequest = new PayRequest();
//        payRequest.setOrderAmount(1L);
//        payRequest.setOrderId(order.getId());
//        payRequest.setOrderName("猛龙商城");
//        payRequest.setSpbillCreateIp(IPAddressUtil.getIPAddress(httpServletRequest));
//        payRequest.setSceneInfo("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://menglongchuxing.cn\",\"wap_name\": \"猛龙商城\"}}");

        PayRequest payRequest = new PayRequest();
        payRequest.setOrderAmount(1L);
        payRequest.setOrderId("xxxxxx17818ydas8");
        payRequest.setOrderName("猛龙商城");
        payRequest.setSpbillCreateIp("183.245.77.242");
        payRequest.setSceneInfo("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://menglongchuxing.cn\",\"wap_name\": \"猛龙商城\"}}");

        return weChatPayService.h5pay(payRequest);

    }

}
