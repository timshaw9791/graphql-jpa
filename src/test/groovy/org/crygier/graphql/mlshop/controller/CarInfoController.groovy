package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.CarInfo
import org.crygier.graphql.mlshop.service.CarInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/7/28 10:48
 */

@SchemaDocumentation("车辆信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class CarInfoController {

    @Autowired
    CarInfoService carInfoService;

    @SchemaDocumentation("添加车辆信息")
    @GRequestMapping(path = "/addcarinfo", method = RequestMethod.POST)
    CarInfo addCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        return this.carInfoService.save(carInfo);
    }

    @SchemaDocumentation("修改车辆信息")
    @GRequestMapping(path = "/updatecarinfo", method = RequestMethod.POST)
    CarInfo updateCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        return this.carInfoService.update(carInfo);
    }

    @SchemaDocumentation("删除车辆信息")
    @GRequestMapping(path = "/removecarinfo", method = RequestMethod.POST)
    CarInfo removeCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        this.carInfoService.deleteById(carInfo.getId());
        return carInfo;
    }
}
