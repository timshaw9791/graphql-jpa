package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Order;

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
     * 保存订单
     * @param id
     * @return
     */
    Order findOne(String id);

}