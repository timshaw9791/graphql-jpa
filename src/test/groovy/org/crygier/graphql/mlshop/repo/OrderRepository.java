package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.enums.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/31 15:53
 */
public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByCreatetimeBetween(Long startTime, Long endTime);

    Long countByCreatetimeBetweenAndOrderStatusEnum(Long startTime, Long endTime, OrderStatusEnum orderStatusEnum);

    Long countByCreatetimeBetween(Long startTime, Long endTime);

}
