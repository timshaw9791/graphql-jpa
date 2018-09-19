package org.crygier.graphql.mlshop.service.impl

import org.crygier.graphql.mlshop.exception.MLShopRunTimeException
import org.crygier.graphql.mlshop.model.FinancialScheme
import org.crygier.graphql.mlshop.model.VehiclePrice
import org.crygier.graphql.mlshop.repo.VehiclePriceRepository
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
        if (vehiclePrice.getShop()==null || vehiclePrice.getCarInfo()==null){
            throw new MLShopRunTimeException("添加失败，信息不完整");
        }

        if (vehiclePrice.getFinancialSchemesItems()!=null){
            //设置更新时间
            for (FinancialScheme financialScheme : vehiclePrice.getFinancialSchemesItems()) {
                if (financialScheme.getSchemeTime() == null || "".equals(financialScheme.getSchemeTime()))
                    financialScheme.setSchemeTime(System.currentTimeMillis());
            }
        }

        return vehiclePriceRepository.save(vehiclePrice);;
    }

    @Override
    VehiclePrice save(VehiclePrice vehiclePrice) {

        if (vehiclePrice.getShop()==null || vehiclePrice.getCarInfo()==null){
            throw new MLShopRunTimeException("添加失败，信息不完整");
        }

        VehiclePrice result = vehiclePriceRepository.findByShopAndCarInfo(vehiclePrice.getShop(), vehiclePrice.getCarInfo());

        if (result!=null){
            throw new MLShopRunTimeException("添加失败，这个门店中的汽车的信息已经存在");
        }

        if (vehiclePrice.getFinancialSchemesItems()!=null){
            //设置更新时间
            for (FinancialScheme financialScheme : vehiclePrice.getFinancialSchemesItems()) {
                if (financialScheme.getSchemeTime() == null || "".equals(financialScheme.getSchemeTime()))
                    financialScheme.setSchemeTime(System.currentTimeMillis());
            }
        }

        return vehiclePriceRepository.save(vehiclePrice);
    }
}
