package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarConfigInfo;

/**
 * @author Curtain
 * @date 2018/7/28 8:42
 */
public interface CarConfigInfoService {

    /**
     * 核对导入的车辆配置是否已经存在   存在则更新
     * @param carConfigInfo
     */
    CarConfigInfo save(CarConfigInfo carConfigInfo);
}
