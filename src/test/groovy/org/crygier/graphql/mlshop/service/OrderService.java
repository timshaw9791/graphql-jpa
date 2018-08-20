package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Order;

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

}
