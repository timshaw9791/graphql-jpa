package org.crygier.graphql.mlshop.schedule;

import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.crygier.graphql.mlshop.util.SpringUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Curtain
 * @date 2018/4/23 10:52
 */

@Component
public class TimeSchedule {

    /*超时订单设置*/
    @Scheduled(cron = "00 00 00 * * ?")
    public void updateCommunicationStatus() {

        CarCommunicationService carCommunicationService = (CarCommunicationService) SpringUtil.getBean("carCommunicationServiceImpl");
        InsuranceCommunicationService insuranceCommunicationService = (InsuranceCommunicationService) SpringUtil.getBean("insuranceCommunicationServiceImpl");

        //查找分配时间在5天前  状态为已回访的订单
//        carCommunicationService.findByDistributeTimeBeforeAndStatus("000",)

    }



}
