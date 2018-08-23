package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.model.enums.OrderStatusEnum;
import org.crygier.graphql.mlshop.repo.OrderRepository;
import org.crygier.graphql.mlshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/31 16:15
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order allocateOrder(String id, Salesman salesman) {
        Order order = findOne(id);
        order.setSalesman(salesman);
        order.setOrderStatusEnum(OrderStatusEnum.PENDING);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByUpdateTime(Long startTime, Long endTIme) {
        return orderRepository.findByCreatetimeBetween(startTime,endTIme);
    }

    @Override
    public Order findOne(String id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
