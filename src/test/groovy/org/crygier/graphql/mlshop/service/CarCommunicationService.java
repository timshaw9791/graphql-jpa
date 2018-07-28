package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Customer;

/**
 * @author Curtain
 * @date 2018/7/28 8:40
 */
public interface CarCommunicationService {

    /**
     * 在用户更新
     * @param customer
     */
    void updateCustomer(Customer customer);

}
