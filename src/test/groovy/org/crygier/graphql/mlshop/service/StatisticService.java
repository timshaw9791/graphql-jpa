package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.bean.CarStatistic;
import org.crygier.graphql.mlshop.bean.OrderStatistic;
import org.crygier.graphql.mlshop.bean.ReceiveStatistic;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/8/20 10:21
 */

public interface StatisticService {

    /**
     * 订单统计
     * @param startTime
     * @param endTime
     * @return
     */
    OrderStatistic orderStatistic(Long startTime, Long endTime);

    /**
     * 车辆统计
     * @param startTime
     * @param endTime
     * @return
     */
    CarStatistic carStatistic(Long startTime,Long endTime);

    /**
     * 接待统计
     * @param startTime
     * @param endTime
     * @return
     */
    ReceiveStatistic receiveStatistic(Long startTime,Long endTime);

    /**
     * 所有统计
     * @param startTime
     * @param endTime
     * @return
     */
    List<Object> allStatistic(Long startTime,Long endTime);

    /**
     * 图表统计
     * @param startTime
     * @return
     */
    List<Object> chartStatistic(Long startTime);
}
