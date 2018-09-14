package org.crygier.graphql.mlshop.service.impl

import org.crygier.graphql.mlshop.model.FinancialScheme
import org.crygier.graphql.mlshop.model.VehiclePrice
import org.crygier.graphql.mlshop.repo.VehiclePriceRepository
import org.crygier.graphql.mlshop.utils.BeanCopyUtil
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
        VehiclePrice result = vehiclePriceRepository.findByShopAndCarInfo(vehiclePrice.getShop(), vehiclePrice.getCarInfo());

        if (result != null) {
            BeanUtils.copyProperties(vehiclePrice, result, BeanCopyUtil.getNullPropertyNames(vehiclePrice));
        }else {
            result = vehiclePrice;
        }

        //设置更新时间
        for (FinancialScheme financialScheme : result.getFinancialSchemesItems()) {
            if (financialScheme.getSchemeTime()==null || "".equals(financialScheme.getSchemeTime()))
                financialScheme.setSchemeTime(System.currentTimeMillis());
        }

        return vehiclePriceRepository.save(result);
    }
}
