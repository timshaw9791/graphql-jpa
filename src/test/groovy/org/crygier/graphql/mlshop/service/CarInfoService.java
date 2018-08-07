package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CarInfo;

/**
 * @author Curtain
 * @date 2018/7/30 17:44
 */
public interface CarInfoService {

    /**
     * 车辆信息更
     * @param carInfo
     * @return
     */
    CarInfo update(CarInfo carInfo);

    /**
     * 保存
     * @param carInfo
     * @return
     */
    CarInfo save(CarInfo carInfo);

    /**
     * 查找一个
     * @param id
     * @return
     */
    CarInfo findOne(String id);

    void deleteById(String id);

}
