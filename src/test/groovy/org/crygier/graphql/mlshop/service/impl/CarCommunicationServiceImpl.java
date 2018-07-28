package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/7/28 9:06
 */

@Service
public class CarCommunicationServiceImpl implements CarCommunicationService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void updateCustomer(Customer customer) {


    }
}
