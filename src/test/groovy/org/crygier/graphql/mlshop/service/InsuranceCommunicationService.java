package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.*;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;

import java.util.Collection;
import java.util.List;

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

    /**
     * 分配
     * @param insuranceCommunicationId
     * @param salesman
     * @return
     */
    InsuranceCommunication allocate(String insuranceCommunicationId,Salesman salesman);

    /**
     * 修改记录
     * @param insuranceCommunication
     * @return
     */
    InsuranceCommunication update(InsuranceCommunication insuranceCommunication);

    /**
     * 删除
     * @param insuranceCommunication
     */
    void deleteById(InsuranceCommunication insuranceCommunication);

    /**
     * 按分配时间前和回访状态查询
     * @param distributeTime
     * @param status
     * @return
     */
    List<InsuranceCommunication> findByDistributeTimeBeforeAndStatus(Long distributeTime, CarCommunicationStatusEnum status);

    /**
     * 保存全部
     * @param collection
     */
    void saveAll(Collection collection);

}
