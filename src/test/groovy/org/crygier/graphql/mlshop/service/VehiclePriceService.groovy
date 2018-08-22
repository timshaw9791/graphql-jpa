package org.crygier.graphql.mlshop.service

import org.crygier.graphql.mlshop.model.VehiclePrice

/**
 * @author Curtain
 * @date 2018/8/22 15:06
 */
interface VehiclePriceService {

    /**
     * 保存车辆价格信息  如有重复 直接覆盖
     * @param vehiclePrice
     * @return
     */
    VehiclePrice save(VehiclePrice vehiclePrice);

    /**
     * 更新
     * @param vehiclePrice
     * @return
     */
    VehiclePrice update(VehiclePrice vehiclePrice)
}