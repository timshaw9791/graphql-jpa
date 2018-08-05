package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Curtain
 * @date 2018/7/31 15:53
 */
public interface OrderRepository extends JpaRepository<Order,String> {
}
