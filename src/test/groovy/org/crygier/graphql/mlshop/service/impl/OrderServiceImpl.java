package org.crygier.graphql.mlshop.service.impl;

import cn.wzvtcsoft.x.bos.domain.util.BeanCopyUtil;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.repo.OrderRepository;
import org.crygier.graphql.mlshop.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Order result = findOne(order.getId());
        BeanUtils.copyProperties(order, result, BeanCopyUtil.getNullPropertyNames(order));
        return orderRepository.save(result);
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
