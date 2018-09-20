package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarInfo;

import java.util.List;

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

    /**
     * 通过时间查询
     * @param startTime
     * @param endTime
     * @return
     */
    List<CarInfo> findByUpdateTime(Long startTime,Long endTime);

    /**
     * 启用汽车信息
     * @param id
     */
    void enableCarInfo(String id);

}
