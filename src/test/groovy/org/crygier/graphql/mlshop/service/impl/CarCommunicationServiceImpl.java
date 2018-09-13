package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.exception.MLShopRunTimeException;
import org.crygier.graphql.mlshop.model.*;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.repo.CarCommunicationRepository;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.SalesmanService;
import org.crygier.graphql.mlshop.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/7/28 9:06
 */

@Service
public class CarCommunicationServiceImpl implements CarCommunicationService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarCommunicationRepository carCommunicationRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private SalesmanService salesmanService;

    @Override
    public CarCommunication addRecord(String carCommunicationId, CommunicationRecord communicationRecord) {
        CarCommunication carCommunication = findOne(carCommunicationId);
        communicationRecord.setAdminist(carCommunication.getAdminist());
        carCommunication.getCommunicationItems().add(communicationRecord);
        carCommunication.setStatus(communicationRecord.getStatus());
        return carCommunicationRepository.save(carCommunication);
    }

    @Override
    public CarCommunication findOne(String id) {
        Optional<CarCommunication> optional = carCommunicationRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        throw new MLShopRunTimeException("未找到车辆沟通单信息");
    }

    @Override
    public void saveAll(Collection collection) {
        carCommunicationRepository.saveAll(collection);
    }

    @Override
    public List<CarCommunication> findByDistributeTimeBeforeAndStatus(Long distributeTime, CarCommunicationStatusEnum status) {
        return carCommunicationRepository.findByDistributeTimeBeforeAndStatus(distributeTime,status);
    }

    @Override
    public CarCommunication deleteById(CarCommunication carCommunication) {
        carCommunication.setDisabled(true);
        return carCommunicationRepository.save(carCommunication);
    }

    @Override
    @Transactional
    public CarCommunication allocate(String carCommunicationId, Salesman salesman) {
        CarCommunication carCommunication = findOne(carCommunicationId);
        salesman = salesmanService.findOne(salesman.getId());
        carCommunication.setSalesman(salesman);
        carCommunication.setDistributeTime(System.currentTimeMillis());
        carCommunication.setStatus(CarCommunicationStatusEnum.B);
        Administ administ = (Administ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        carCommunication.setAdminist(administ);
        if (salesman.getTel()==null || "".equals(salesman.getTel())){
            throw new MLShopRunTimeException("分配失败，业务员手机号为空，不能在分配时发送短信");
        }
        verificationService.visitCode(salesman.getTel(),carCommunication.getNumber());
        CarCommunication result = carCommunicationRepository.save(carCommunication);
        return result;
    }

    @Override
    public CarCommunication save(CarCommunication carCommunication) {
        updateCustomer(carCommunication.getCustomer());
        return carCommunicationRepository.save(carCommunication);
    }

    @Override
    public void updateCustomer(Customer customer) {
        Customer result = customerRepository.findById(customer.getId()).get();
        CustomerLevelEnum value = customer.getLevel();
        if (value != null && !("".equals(value.getValue())) && !(value.equals(result.getLevel().getValue()))) {
            result.setLevel(value);
            customerRepository.save(result);
        }
    }
}
