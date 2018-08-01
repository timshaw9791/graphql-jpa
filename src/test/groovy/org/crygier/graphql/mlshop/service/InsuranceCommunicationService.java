package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.InsuranceCommunication;

/**
 * @author Curtain
 * @date 2018/8/1 8:40
 */
public interface InsuranceCommunicationService {

    /**
     * 用户等级更新
     *
     * @param customer
     */
    void updateCustomer(Customer customer);

    /**
     * 保存保险沟通信息
     *
     * @param insuranceCommunication
     * @return
     */
    InsuranceCommunication save(InsuranceCommunication insuranceCommunication);

    /**
     * 添加保险沟通记录
     *
     * @param insuranceCommunicationId
     * @param communicationRecord
     * @return
     */
    InsuranceCommunication addRecord(String insuranceCommunicationId, CommunicationRecord communicationRecord);

}
