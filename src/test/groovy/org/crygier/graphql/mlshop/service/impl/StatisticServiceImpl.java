package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.bean.CarStatistic;
import org.crygier.graphql.mlshop.bean.OrderStatistic;
import org.crygier.graphql.mlshop.bean.ReceiveStatistic;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.model.PayScheme;
import org.crygier.graphql.mlshop.model.enums.*;
import org.crygier.graphql.mlshop.repo.*;
import org.crygier.graphql.mlshop.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Curtain
 * @date 2018/8/20 10:25
 */

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private CarCommunicationRepository carCommunicationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;


    @Override
    public OrderStatistic orderStatistic(Long startTime, Long endTime) {
        //初始化数据
        OrderStatistic result = new OrderStatistic();

        /*新订单*/
        Long newOrder = 0L;

        /*完成订单*/
        Long finishOrder = 0L;

        /*待退款订单*/
        Long refundOrder = 0L;

        /*进行中订单(已支付 新订单)*/
        Long underwayOrder = 0L;

        /*待分配订单*/
        Long waitAllocate = 0L;

        /*销售额*/
        Long sellTotal = 0L;

        /*总订单数*/
        Long orderTotal = 0L;


        List<Order> orderList = orderRepository.findByCreatetimeBetween(startTime, endTime);

        for (Order order : orderList) {
            //新订单
            if (OrderStatusEnum.NEW.equals(order.getOrderStatusEnum())) {
                newOrder++;
                if (OrderPayStatusEnum.PAID.equals(order.getPayStatusEnum())){
                    underwayOrder++;
                }
            }

            //已完成订单
            if (OrderStatusEnum.FINISH.equals(order.getOrderStatusEnum())) {
                finishOrder++;
                //销售额
                if (order.getPaySchemes().size() > 0) {
                    sellTotal += ((PayScheme) order.getPaySchemes().toArray()[0]).getPrice();
                }
            }

            //待分配订单
            if (OrderAllocateStatusEnum.WAIT.equals(order.getOrderAllocateStatusEnum())) {
                waitAllocate++;
            }

            //退款订单
            //已完成订单
            if (OrderStatusEnum.REFUND.equals(order.getOrderStatusEnum())) {
                refundOrder++;
            }

            orderTotal++;
        }

        result.setFinishOrder(finishOrder);
        result.setRefundOrder(refundOrder);
        result.setNewOrder(newOrder);
        result.setSellTotal(sellTotal);
        result.setUnderwayOrder(underwayOrder);
        result.setWaitAllocate(waitAllocate);
        result.setOrderTotal(orderTotal);

        return result;
    }

    @Override
    public CarStatistic carStatistic(Long startTime, Long endTime) {

        //初始化数据
        CarStatistic carStatistic = new CarStatistic();

        /*上架车辆数量*/
        Long upCar;

        /*下架车辆数量*/
        Long downCar;

        /*待完善车辆数据*/
        Long toPerfectCar = 0L;

        /*总车辆*/
        Long carTotal = 0L;

        //暂时查询所有  不需要依据时间
        upCar = carInfoRepository.countByDisabled(false);

        downCar = carInfoRepository.countByDisabled(true);

        carTotal = carInfoRepository.countByPerfectState(false);

        carStatistic.setCarTotal(upCar + downCar);
        carStatistic.setDownCar(downCar);
        carStatistic.setUpCar(upCar);
        carStatistic.setToPerfectCar(toPerfectCar);

        return carStatistic;
    }

    @Override
    public List<Object> chartStatistic(Long startTime) {

        //初始化结果集
        List<Object> result = new ArrayList<>();

        ArrayList<Long> carSaleList = new ArrayList<>();
        ArrayList<Long> orderCountList = new ArrayList<>();
        ArrayList<Long> orderAmountList = new ArrayList<>();
        ArrayList<Long> customerCountList = new ArrayList<>();
        Map<String, ArrayList<Long>> map = new HashMap<>();


        //设定初始结束时间
        Long endTime = startTime + 86400000;

        //统计 30天内 每天的数据
        for (int i = 0; i < 30; i++) {
            Long carSaleCount = 0L;
            Long orderCount = 0L;
            Long orderAmount = 0L;
            Long customerCount = 0L;
            List<Order> orderList = orderRepository.findByCreatetimeBetween(startTime, endTime);

            //统计销售数量

            for (Order order : orderList) {
                if (order.getCount() != null) {
                    carSaleCount = order.getCount() + carSaleCount;
                }
                orderCount++;
                if (order.getPaySchemes().size() > 0) {
                    orderAmount += ((PayScheme) order.getPaySchemes().toArray()[0]).getPrice();
                }

            }


            carSaleList.add(carSaleCount);
            orderCountList.add(orderCount);
            orderAmountList.add(orderAmount);
            customerCountList.add(carSaleCount);

            startTime = endTime;
            endTime += 86400000;
        }

        map.put("carSaleList", carSaleList);
        map.put("orderCountList", orderCountList);
        map.put("orderAmountList", orderAmountList);
        map.put("customerCountList", customerCountList);

        result.add(map);

        return result;
    }

    @Override
    public List<Object> allStatistic(Long startTime, Long endTime) {

        List<Object> list = new ArrayList<>();

        list.add(orderStatistic(startTime, endTime));
        list.add(carStatistic(startTime, endTime));
        list.add(receiveStatistic(startTime, endTime));
        list.add(chartStatistic(startTime));

        return list;
    }

    @Override
    public ReceiveStatistic receiveStatistic(Long startTime, Long endTime) {

        //初始化数据
        ReceiveStatistic receiveStatistic = new ReceiveStatistic();

        /*销售人员*/
        Long salesManTotal;

        /*来访客户*/
        Long visitCustomerTotal;

        /*回访客户*/
        Long replyCustomerTotal;

        /*待分配客户*/
        Long waitAllocateCustomer;

        /*待回访客户*/
        Long waitReplyCustomer;

        /*已回访客户*/
        Long replyCustomer;

        /*订单客户*/
        Long orderCustomer;

        /*成交客户*/
        Long dealCustomer;

        /*保险客户*/
        Long insuranceCustomer;

        /*总门店*/
        Long shopTotal;

        /*总客户*/
        Long customerTotal;


        salesManTotal = salesmanRepository.count();

        visitCustomerTotal = carCommunicationRepository.countByUpdatetimeBetweenAndType(0L, 4099651200000L, CarCommunicationTypeEnum.A);

        replyCustomerTotal = carCommunicationRepository.countByUpdatetimeBetweenAndType(0L, 4099651200000L, CarCommunicationTypeEnum.B);

        waitReplyCustomer = carCommunicationRepository.countByUpdatetimeBetweenAndStatus(startTime, endTime, CarCommunicationStatusEnum.B);

        waitAllocateCustomer = carCommunicationRepository.countByUpdatetimeBetweenAndStatus(startTime, endTime, CarCommunicationStatusEnum.A);

        replyCustomer = carCommunicationRepository.countByUpdatetimeBetweenAndStatus(startTime, endTime, CarCommunicationStatusEnum.C);

        orderCustomer = orderRepository.countByCreatetimeBetween(startTime, endTime);

        dealCustomer = orderRepository.countByCreatetimeBetweenAndOrderStatusEnum(startTime, endTime, OrderStatusEnum.FINISH);

        shopTotal = shopRepository.count();

        customerTotal = customerRepository.count();

        insuranceCustomer = insuranceRepository.count();

        receiveStatistic.setCustomerTotal(customerTotal);
        receiveStatistic.setDealCustomer(dealCustomer);
        receiveStatistic.setInsuranceCustomer(insuranceCustomer);
        receiveStatistic.setOrderCustomer(orderCustomer);
        receiveStatistic.setReplyCustomer(replyCustomer);
        receiveStatistic.setReplyCustomerTotal(replyCustomerTotal);
        receiveStatistic.setSalesManTotal(salesManTotal);
        receiveStatistic.setShopTotal(shopTotal);
        receiveStatistic.setWaitAllocateCustomer(waitAllocateCustomer);
        receiveStatistic.setWaitReplyCustomer(waitReplyCustomer);
        receiveStatistic.setVisitCustomerTotal(visitCustomerTotal);

        return receiveStatistic;
    }
}
