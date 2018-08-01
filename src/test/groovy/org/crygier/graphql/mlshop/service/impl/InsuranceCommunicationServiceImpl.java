package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.InsuranceCommunication;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.repo.InsuranceCommunicationRepository;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

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
        updateCustomer(insuranceCommunication.getCustomer());
        return insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @Override
    public InsuranceCommunication addRecord(String insuranceCommunicationId, CommunicationRecord communicationRecord) {
        InsuranceCommunication insuranceCommunication = insuranceCommunicationRepository.findById(insuranceCommunicationId).get();
        insuranceCommunication.getCommunicationItems().add(communicationRecord);
        insuranceCommunication.setStatus(communicationRecord.getStatus());
        return insuranceCommunicationRepository.save(insuranceCommunication);
    }
}
