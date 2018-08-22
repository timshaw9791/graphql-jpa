package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.CarBrandIcon;
import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.service.CarBrandIconService;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/31 10:29
 */
@SchemaDocumentation("回访单相关修改操作")
@GRestController("mlshop")
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, maxAge = 1800L, allowedHeaders = "*")
public class ModifyController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private CarBrandIconService carBrandIconService;


    @RequestMapping(value = "/updatecarinfo")
    public Object updateCarInfo(@RequestBody CarInfo carInfo) {
        return carInfoService.update(carInfo);
    }

    @RequestMapping(value = "/addcarinfo")
    public Object saveCarInfo(@RequestBody CarInfo carInfo){
        return carInfoService.save(carInfo);
    }

    @RequestMapping("/statistic")
    public Object statistic(@RequestParam("starttime") Long startTime,@RequestParam("endtime") Long endTime){
        return statisticService.allStatistic(startTime,endTime);
    }

    @RequestMapping("/addorder")
    public Order order(@RequestBody Order order) {
        return orderService.save(order);
    }

    @RequestMapping(path = "/addcarbrandicon", method = RequestMethod.POST)
    CarBrandIcon addCarBrandIcon(@RequestParam(name = "carbrandicon", required = true) CarBrandIcon carBrandIcon) {
        return carBrandIconService.save(carBrandIcon);
    }

    @RequestMapping(path = "/saveallcarbrandicon", method = RequestMethod.POST)
    void saveAllCarBrandIcon(@RequestParam(name = "carbrandicon", required = true) List<CarBrandIcon> carBrandIcons) {
        carBrandIconService.saveAll(carBrandIcons);
    }

    @RequestMapping(path = "/getinfo")
    void getinfo(Principal principal){
        System.out.println(principal.getName());
    }
}
