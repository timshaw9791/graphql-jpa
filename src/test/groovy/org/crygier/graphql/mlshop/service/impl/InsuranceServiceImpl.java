package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Insurance;
import org.crygier.graphql.mlshop.model.InsuranceCommunication;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.repo.InsuranceRepository;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.crygier.graphql.mlshop.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Curtain
 * @date 2018/8/2 11:11
 */
@Service
public class InsuranceServiceImpl implements InsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private InsuranceCommunicationService insuranceCommunicationService;

    @Override
    public Insurance findOne(String id) {
        return insuranceRepository.findById(id).get();
    }

    @Override
    public void deleteById(Insurance insurance) {
        insurance.setDisabled(true);
        insuranceRepository.save(insurance);
    }

    @Override
    public Insurance update(Insurance insurance) {

        insurance.getCustomerName();


        return insuranceRepository.save(insurance);


    }

    @Override
    @Transactional
    public Insurance save(Insurance insurance) {
        InsuranceCommunication insuranceCommunication = new InsuranceCommunication();
        //todo 去掉回访记录中 customername  等信息的保存  因为 insurance中已经存在
        insuranceCommunication.setCustomerName(insurance.getCustomerName());
        insuranceCommunication.setStatus(CarCommunicationStatusEnum.D);
        insuranceCommunication.setTel(insurance.getCustomerTel());
        insuranceCommunication.setInsurance(insurance);
        insuranceCommunicationService.save(insuranceCommunication);
        return insuranceRepository.save(insurance);
    }
}
