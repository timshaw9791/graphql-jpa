package org.crygier.graphql.mlshop.schedule;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.InsuranceCommunication;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.crygier.graphql.mlshop.utils.SpringUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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
        Long time = System.currentTimeMillis()-(86400000*5);
        List<CarCommunication> carCommunicationList = carCommunicationService.findByDistributeTimeBeforeAndStatus(time, CarCommunicationStatusEnum.C);
        List<InsuranceCommunication> insuranceCommunicationList = insuranceCommunicationService.findByDistributeTimeBeforeAndStatus(time, CarCommunicationStatusEnum.C);


        //遍历 将状态改为已回访
        for (CarCommunication carCommunication : carCommunicationList){
            carCommunication.setStatus(CarCommunicationStatusEnum.A);
        }

        for (InsuranceCommunication insuranceCommunication:insuranceCommunicationList){
            insuranceCommunication.setStatus(CarCommunicationStatusEnum.A);
        }

        carCommunicationService.saveAll(carCommunicationList);
        insuranceCommunicationService.saveAll(insuranceCommunicationList);
    }


    /*保险到期*/
    @Scheduled(cron = "00 00 00 * * ?")
    public void updateInsuranceCommunication() {
        InsuranceCommunicationService insuranceCommunicationService = (InsuranceCommunicationService) SpringUtil.getBean("insuranceCommunicationServiceImpl");
        List<InsuranceCommunication> insuranceCommunicationList = insuranceCommunicationService.findAll();

        //11个月的毫秒数
        final long timeStamp = 28512000000l;

        for (InsuranceCommunication insuranceCommunication : insuranceCommunicationList){
            //保险时间
            Long time = insuranceCommunication.getInsurance().getTime();

            //如果保险时间 距离现在大于 11 个月  则将状态改为带分配

            if (time+timeStamp<System.currentTimeMillis()){
                insuranceCommunication.setStatus(CarCommunicationStatusEnum.A);
                insuranceCommunicationService.save(insuranceCommunication);
            }
        }
    }


}
