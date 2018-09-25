package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;

import java.util.Collection;
import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/28 8:40
 */
public interface    CarCommunicationService {

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

    CarCommunication update(CarCommunication carCommunication);

    /**
     * 添加买车沟通记录
     *
     * @param carCommunicationId
     * @param communicationRecord
     * @return
     */
    CarCommunication addRecord(String carCommunicationId, CommunicationRecord communicationRecord);

    /**
     * 分配
     * @param carCommunicationId
     * @param salesman
     * @return
     */
    CarCommunication allocate(String carCommunicationId,Salesman salesman);

    /**
     * 删除（禁用  不显示）
     * @param carCommunication
     * @return
     */
    CarCommunication deleteById(CarCommunication carCommunication);

    /**
     * 按分配时间前和回访状态查询
     * @param distributeTime
     * @param status
     * @return
     */
    List<CarCommunication> findByDistributeTimeBeforeAndStatus(Long distributeTime,CarCommunicationStatusEnum status);

    /**
     * 保存全部
     * @param collection
     */
    void saveAll(Collection collection);


    /**
     * 查找单个  id
     * @param id
     */
    CarCommunication findOne(String id);
}
