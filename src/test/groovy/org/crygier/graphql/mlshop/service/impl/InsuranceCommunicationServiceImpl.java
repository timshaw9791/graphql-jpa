package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.*;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.repo.InsuranceCommunicationRepository;
import org.crygier.graphql.mlshop.repo.SalesmanRepository;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.crygier.graphql.mlshop.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author Curtain
 * @date 2018/8/1 9:00
 */
@Service
public class InsuranceCommunicationServiceImpl implements InsuranceCommunicationService {

    @Autowired
    private InsuranceCommunicationRepository insuranceCommunicationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private SalesmanRepository salesmanRepository;


    @Override
    public void updateCustomer(Customer customer) {
        Customer result = customerRepository.findById(customer.getId()).get();
        CustomerLevelEnum value = customer.getLevel();
        if (value != null && !("".equals(value.getValue())) && !(value.equals(result.getLevel().getValue()))) {
            result.setLevel(value);
            customerRepository.save(result);
        }
    }

    @Override
    public InsuranceCommunication save(InsuranceCommunication insuranceCommunication) {
//        updateCustomer(insuranceCommunication.getCustomer());
        return insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @Override
    public List<InsuranceCommunication> findAll() {
        return insuranceCommunicationRepository.findAll();
    }

    @Override
    public void saveAll(Collection collection) {
        insuranceCommunicationRepository.saveAll(collection);
    }

    @Override
    public List<InsuranceCommunication> findByDistributeTimeBeforeAndStatus(Long distributeTime, CarCommunicationStatusEnum status) {
        return insuranceCommunicationRepository.findByDistributeTimeBeforeAndStatus(distributeTime,status);
    }

    @Override
    public void deleteById(InsuranceCommunication insuranceCommunication) {
        insuranceCommunication.setDisabled(true);
        insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @Override
    public InsuranceCommunication update(InsuranceCommunication insuranceCommunication) {
        return insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @Override
    public InsuranceCommunication allocate(String insuranceCommunicationId, Salesman salesman) {
        InsuranceCommunication insuranceCommunication = insuranceCommunicationRepository.findById(insuranceCommunicationId).get();
        salesman=salesmanRepository.findById(salesman.getId()).get();
        insuranceCommunication.distribute(salesman);
        insuranceCommunication.setDistributeTime(System.currentTimeMillis());
        Administ administ = (Administ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        insuranceCommunication.setAdminist(administ);
        InsuranceCommunication result = insuranceCommunicationRepository.save(insuranceCommunication);
        verificationService.visitCode(salesman.getTel(),insuranceCommunication.getNumber());
        return result;
    }



    @Override
    public InsuranceCommunication addRecord(String insuranceCommunicationId, CommunicationRecord communicationRecord) {
        InsuranceCommunication insuranceCommunication = insuranceCommunicationRepository.findById(insuranceCommunicationId).get();
        //设置默认客户等级  默认  因为不需要展示
//        communicationRecord.setLevel(CustomerLevelEnum.A);
        communicationRecord.setAdminist(insuranceCommunication.getAdminist());
        insuranceCommunication.getCommunicationItems().add(communicationRecord);
        insuranceCommunication.setStatus(communicationRecord.getStatus());
        return insuranceCommunicationRepository.save(insuranceCommunication);
    }
}
