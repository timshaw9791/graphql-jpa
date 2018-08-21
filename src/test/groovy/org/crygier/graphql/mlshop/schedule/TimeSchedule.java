package org.crygier.graphql.mlshop.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Curtain
 * @date 2018/4/23 10:52
 */

@Component
public class TimeSchedule {

    /*超时订单设置*/
    @Scheduled(cron = "00 00 00 * * ?")
    public void updateLaundryOrderStatus() {

//        LaundryOrderService orderService = (LaundryOrderService) SpringUtil.getBean("laundryOrderServiceImpl");
//
//        //找到所有的入站订单   普通洗衣
//        List<LaundryOrder> laundryOrders = orderService.findByTypeAndStatus(OrderTypeEnum.LAUNDRY.getCode(), OrderStatusEnum.INBOUND.getCode());
//
//        for (LaundryOrder order : laundryOrders) {
//            //判断入站时间是否大于7天
//            Long inboundTime = order.getInboundTime();
//            if ((inboundTime < (System.currentTimeMillis() - 7 * DateUtil.DAY_TIME_STAMP)) && (LaundryOrderTimeEnum.NORMAL.getCode().equals(order.getTimeOut()))) {
//                order.setTimeOut(LaundryOrderTimeEnum.TIME_OUT.getCode());
//            }
//        }
//
//        orderService.saveAll(laundryOrders);
//
//
//        //找到所有的入站订单   高端洗护
//        laundryOrders = orderService.findByTypeAndStatus(OrderTypeEnum.HIGH_LAUNDRY.getCode(), OrderStatusEnum.INBOUND.getCode());
//
//        for (LaundryOrder order : laundryOrders) {
//            //判断入站时间是否大于7天
//            Long inboundTime = order.getInboundTime();
//            if ((inboundTime < (System.currentTimeMillis() - 7 * DateUtil.DAY_TIME_STAMP)) && (LaundryOrderTimeEnum.NORMAL.getCode().equals(order.getTimeOut()))) {
//                order.setTimeOut(LaundryOrderTimeEnum.TIME_OUT.getCode());
//            }
//        }
//        orderService.saveAll(laundryOrders);
    }



}
