package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude;
import org.crygier.graphql.mlshop.model.Order
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.repo.OrderRepository
import org.crygier.graphql.mlshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/14 10:10
 */

@SchemaDocumentation("订单信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @SchemaDocumentation("添加订单")
    @GRequestMapping(path = "/addorder", method = RequestMethod.POST)
    Order addOrder(@RequestParam(name = "order", required = true) Order order) {
        return this.orderRepository.save(order);
    }

    @SchemaDocumentation("修改订单")
    @Exclude
    @GRequestMapping(path = "/updateorder", method = RequestMethod.POST)
    Order updateOrder(@RequestParam(name = "order", required = true) Order order) {
        return this.orderRepository.save(order);
    }

    @SchemaDocumentation("分配订单")
    @GRequestMapping("/allocateorder")
    Order allocateOrder(@RequestParam(name = "id", required = true) String id,
                        @RequestParam(name = "salesman", required = true) Salesman salesman) {
        return orderService.allocateOrder(id, salesman);
    }

    @SchemaDocumentation("完结订单")
    @GRequestMapping("/finishorder")
    Order finishOrder(@RequestParam(name = "id", required = true) String id) {
        return orderService.finish(id);
    }

    @SchemaDocumentation("审核订单")
    @GRequestMapping("/auditorder")
    Order auditOrder(@RequestParam(name = "id", required = true) String id,
                     @RequestParam(name = "flag", required = true) Boolean flag,
                     @RequestParam(name = "cause", required = true) String cause) {
        return orderService.audit(id, flag, cause);
    }
}
