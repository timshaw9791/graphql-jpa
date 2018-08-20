package org.crygier.graphql.mlshop.bean;

/**
 * 订单统计
 * @author Curtain
 * @date 2018/8/8 15:44
 */
public class OrderStatistic {

    /*新订单*/
    Long newOrder;

    /*完成订单*/
    Long finishOrder;

    /*待退款订单*/
    Long refundOrder;

    /*进行中订单(已支付 新订单)*/
    Long underwayOrder;

    /*待分配订单*/
    Long waitAllocate;

    /*销售额*/
    Long sellTotal;

    /*总订单数*/
    Long orderTotal;

}
