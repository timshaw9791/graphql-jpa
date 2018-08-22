package org.crygier.graphql.mlshop.service.impl

import org.crygier.graphql.mlshop.model.VehiclePrice
import org.crygier.graphql.mlshop.repo.VehiclePriceRepository
import org.crygier.graphql.mlshop.util.BeanCopyUtil
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/**
 * @author Curtain
 * @date 2018/8/22 15:07
 */

@Service
class VehiclePriceServiceImpl implements org.crygier.graphql.mlshop.service.VehiclePriceService {

    @Autowired
    private VehiclePriceRepository vehiclePriceRepository;

    @Override
    VehiclePrice update(VehiclePrice vehiclePrice) {
        return vehiclePriceRepository.save(vehiclePrice);
    }

    @Override
    VehiclePrice save(VehiclePrice vehiclePrice) {
        VehiclePrice v = vehiclePriceRepository.findByShopAndCarInfo(vehiclePrice.getShop(),vehiclePrice.getCarInfo());

        if (v!=null){
           BeanUtils.copyProperties(vehiclePrice,v,BeanCopyUtil.getNullPropertyNames(vehiclePrice));
        }

        return vehiclePriceRepository.save(v);
    }
}
