package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.Salesman;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/31 16:06
 */
public interface OrderService {

    /**
     * 创建订单
     * @param order
     * @return
     */
    Order save(Order order);

    /**
     * 修改订单
     * @param order
     * @return
     */
    Order update(Order order);

    /**
     * 查找订单
     * @param id
     * @return
     */
    Order findOne(String id);

    /**
     * 通过时间段查找订单
     * @param startTime
     * @param endTIme
     * @return
     */
    List<Order> findByUpdateTime(Long startTime,Long endTIme);


    /**
     * 订单分配
     * @param id
     * @param salesman
     * @return
     */
    Order allocateOrder(String id, Salesman salesman);

    /**
     * 订单支付
     * @param id
     * @return
     */
    Order paid(String id,String payMode);

    /**
     * 退款
     * @param id
     * @return
     */
    Order refund(String id);

    /**
     * 审核
     * @param id
     * @return
     */
    Order audit(String id,Boolean flag,String cause);

    /**
     * 完结
     * @param id
     * @return
     */
    Order finish(String id);

    /**
     * 砍价成功设置
     * @param id
     */
    void bargain(String id);
}
