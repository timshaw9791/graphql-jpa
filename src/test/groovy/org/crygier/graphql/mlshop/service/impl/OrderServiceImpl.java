package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Administ;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.model.enums.OrderAllocateStatusEnum;
import org.crygier.graphql.mlshop.model.enums.OrderPayStatusEnum;
import org.crygier.graphql.mlshop.model.enums.OrderStatusEnum;
import org.crygier.graphql.mlshop.repo.OrderRepository;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/31 16:15
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PayService payService;

    @Override
    public Order update(Order order) {
        Order result = findOne(order.getId());

        //保证支付状态 砍价状态  不被编辑订单修改
        order.setPayStatusEnum(result.getPayStatusEnum());
        order.setBargainSuccess(result.getBargainSuccess());

        return orderRepository.save(order);
    }

    @Override
    public Order paid(String id) {
        Order order = findOne(id);

        order.setPayStatusEnum(OrderPayStatusEnum.PAID);

        return orderRepository.save(order);
    }

    @Override
    public Order refund(String id) {
        Order order = findOne(id);
        order.setPayStatusEnum(OrderPayStatusEnum.REFUND);
        order.setOrderStatusEnum(OrderStatusEnum.REFUND);
        order.setRefundTime(System.currentTimeMillis());
        Administ administ = (Administ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        order.setAdminist(administ);
        return orderRepository.save(order);
    }

    @Override
    public void bargain(String id) {
        Order order = findOne(id);
        order.setBargainSuccess(true);
        orderRepository.save(order);
    }

    @Override
    public Order finish(String id) {
        Order order = findOne(id);
        if (!(OrderPayStatusEnum.PAID.equals(order.getPayStatusEnum()))){
            throw new RuntimeException("订单支付状态不正确，不能完结");
        }
        if (!(OrderStatusEnum.PENDING.equals(order.getOrderStatusEnum()))){
            throw new RuntimeException("订单状态不正确，不能完结");
        }
        if (!(OrderAllocateStatusEnum.ALREADY.equals(order.getOrderAllocateStatusEnum()))){
            throw new RuntimeException("订单分配状态不正确，不能完结");
        }

        order.setOrderStatusEnum(OrderStatusEnum.FINISH);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order audit(String id,Boolean flag,String cause) {
        Order order = findOne(id);
        if (!(OrderPayStatusEnum.PAID.equals(order.getPayStatusEnum()))){
            throw new RuntimeException("订单支付状态不正确，不能审核");
        }
        if (!(OrderStatusEnum.NEW.equals(order.getOrderStatusEnum()))){
            throw new RuntimeException("订单状态不正确，不能审核");
        }
        //通过
        if (flag){
            order.setAudit(true);
            order.setOrderStatusEnum(OrderStatusEnum.PENDING);

        }else {
            //不通过  退款
            payService.refund(order.getId());
            order.setCause(cause);

        }
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
        //数据初始化   支付状态  砍价状态
        order.setPayStatusEnum(OrderPayStatusEnum.WAIT);
        order.setBargainSuccess(false);
        return orderRepository.save(order);
    }
}
