package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;

/**
 * @author Curtain
 * @date 2018/7/28 8:40
 */
public interface CarCommunicationService {

    /**
     * 用户等级更新
     *
     * @param customer
     */
    void updateCustomer(Customer customer);

    /**
     * 保存买车沟通信息
     *
     * @param carCommunication
     * @return
     */
    CarCommunication save(CarCommunication carCommunication);

    /**
     * 添加买车沟通记录
     *
     * @param carCommunicationId
     * @param communicationRecord
     * @return
     */
    CarCommunication addRecord(String carCommunicationId, CommunicationRecord communicationRecord);


}
