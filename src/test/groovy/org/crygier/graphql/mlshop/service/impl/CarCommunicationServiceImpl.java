package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.repo.CarCommunicationRepository;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.SalesmanService;
import org.crygier.graphql.mlshop.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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
        CarCommunication carCommunication = carCommunicationRepository.findById(carCommunicationId).get();
        carCommunication.getCommunicationItems().add(communicationRecord);
        carCommunication.setStatus(communicationRecord.getStatus());
        return carCommunicationRepository.save(carCommunication);
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
    public CarCommunication allocate(String carCommunicationId, Salesman salesman) {
        CarCommunication carCommunication = carCommunicationRepository.findById(carCommunicationId).get();
        salesman = salesmanService.findOne(salesman.getId());
        carCommunication.setSalesman(salesman);
        carCommunication.setDistributeTime(System.currentTimeMillis());
        carCommunication.setStatus(CarCommunicationStatusEnum.B);
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
