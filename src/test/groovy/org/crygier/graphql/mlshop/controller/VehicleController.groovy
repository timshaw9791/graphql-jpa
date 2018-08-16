package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude
import org.crygier.graphql.mlshop.model.VehiclePrice
import org.crygier.graphql.mlshop.repo.VehiclePriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
/**
 * @author Curtain
 * @date 2018/8/13 15:41
 */
@SchemaDocumentation("车辆价格信息")
@GRestController("mlshop")
@RestController
@CompileStatic
class VehicleController {

    @Autowired
    private VehiclePriceRepository vehiclePriceRepository;

    @SchemaDocumentation("添加车辆价格信息")
    @GRequestMapping(path = "/addvehicleprice", method = RequestMethod.POST)
    VehiclePrice addVehiclePrice(@RequestParam(name = "vehicleprice", required = true) VehiclePrice vehiclePrice) {
        return this.vehiclePriceRepository.save(vehiclePrice);
    }

    @SchemaDocumentation("修改车辆价格信息")
    @Exclude
    @GRequestMapping(path = "/updatevehicleprice", method = RequestMethod.POST)
    VehiclePrice updateVehiclePrice(@RequestParam(name = "vehicleprice", required = true) VehiclePrice vehiclePrice) {
        return this.vehiclePriceRepository.save(vehiclePrice);
    }

    @SchemaDocumentation("删除车辆价格信息")
    @GRequestMapping(path = "/removevehicleprice", method = RequestMethod.POST)
    VehiclePrice removeVehiclePrice(@RequestParam(name = "vehicleprice", required = true) VehiclePrice vehiclePrice) {
        vehiclePrice.setDisabled(true);
        return this.vehiclePriceRepository.save(vehiclePrice);
    }
}
