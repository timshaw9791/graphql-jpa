package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude;
import org.crygier.graphql.mlshop.model.ConcernCar;
import org.crygier.graphql.mlshop.model.ConcernShop;
import org.crygier.graphql.mlshop.service.ConcernCarService;
import org.crygier.graphql.mlshop.service.ConcernShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/8 9:11
 */

@SchemaDocumentation("用户关注信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class ConcernController {
    @Autowired
    ConcernShopService concernShopService;

    @Autowired
    ConcernCarService concernCarService;

    @SchemaDocumentation("关注车辆")
    @Exclude
    @GRequestMapping(path = "/concerncar", method = RequestMethod.POST)
    ConcernCar concernCar(@RequestParam(name = "concerncar", required = true) ConcernCar concernCar) {
        return this.concernCarService.concern(concernCar);
    }

    @SchemaDocumentation("取消关注车辆")
    @Exclude
    @GRequestMapping(path = "/cancelcar", method = RequestMethod.POST)
    ConcernCar cancelCar(@RequestParam(name = "concerncar", required = true) ConcernCar concernCar) {
        concernCarService.cancel(concernCar.getId());
        return concernCar;
    }

    @SchemaDocumentation("关注门店")
    @Exclude
    @GRequestMapping(path = "/concernshop", method = RequestMethod.POST)
    ConcernShop concernShop(@RequestParam(name = "concernshop", required = true) ConcernShop concernShop) {
        return this.concernShopService.concern(concernShop);
    }

    @SchemaDocumentation("取消关注门店")
    @Exclude
    @GRequestMapping(path = "/cancelshop", method = RequestMethod.POST)
    ConcernShop cancelShop(@RequestParam(name = "concernshop", required = true) ConcernShop concernShop) {
        concernShopService.cancel(concernShop.getId());
        return concernShop;
    }
}
