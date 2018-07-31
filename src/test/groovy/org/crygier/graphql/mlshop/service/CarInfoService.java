package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarInfo;

/**
 * @author Curtain
 * @date 2018/7/30 17:44
 */
public interface CarInfoService {

    /**
     * 车辆信息更新
     * @param carInfo
     * @return
     */
    CarInfo update(CarInfo carInfo);
}
