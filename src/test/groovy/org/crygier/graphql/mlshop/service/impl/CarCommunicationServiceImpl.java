package org.crygier.graphql.mlshop.service.impl;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.repo.CarCommunicationRepository;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

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

    @Override
    public CarCommunication addRecord(String carCommunicationId, CommunicationRecord communicationRecord) {
        CarCommunication carCommunication = carCommunicationRepository.findById(carCommunicationId).get();
        carCommunication.getCommunicationItems().add(communicationRecord);
        return carCommunicationRepository.save(carCommunication);
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
