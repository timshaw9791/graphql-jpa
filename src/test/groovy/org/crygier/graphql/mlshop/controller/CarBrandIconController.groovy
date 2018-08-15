package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.CarBrandIcon;
import org.crygier.graphql.mlshop.service.CarBrandIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/14 10:12
 */

@SchemaDocumentation("车辆优选（品牌）信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class CarBrandIconController {

    @Autowired
    private CarBrandIconService carBrandIconService;

    @SchemaDocumentation("增加一条车辆品牌信息")
    @GRequestMapping(path = "/addcarbrandicon", method = RequestMethod.POST)
    CarBrandIcon addCarBrandIcon(@RequestParam(name = "carbrandicon", required = true) CarBrandIcon carBrandIcon) {
        return carBrandIconService.save(carBrandIcon);
    }

    @SchemaDocumentation("保存所有已选车辆品牌信息")
    @GRequestMapping(path = "/saveallcarbrandicon", method = RequestMethod.POST)
    CarBrandIcon saveAllCarBrandIcon(@RequestParam(name = "carbrandicon", required = true) Collection<CarBrandIcon> carBrandIcons) {
        carBrandIconService.saveAll(carBrandIcons);
        return null;
    }
}
