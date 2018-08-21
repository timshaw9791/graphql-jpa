package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.*;
import org.crygier.graphql.mlshop.service.*;
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
    private CarInfoService carService;

    @Autowired
    private CarCommunicationService carCommunicationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CarBrandIconService carBrandIconService;

    @Autowired
    private InsuranceCommunicationService insuranceCommunicationService;

    @Autowired
    private StatisticService statisticService;

    @RequestMapping(value = "/updatecarinfo")
    public Object updateCarInfo(@RequestBody CarInfo carInfo) {
        return carService.update(carInfo);
    }

    @RequestMapping(value = "/addcarinfo")
    public Object saveCarInfo(@RequestBody CarInfo carInfo){
        return carService.save(carInfo);
    }

    @RequestMapping("/addcarcommunication")
    public Object addCarCommunication(@RequestBody CarCommunication carCommunication) {
        return this.carCommunicationService.save(carCommunication);
    }

    @RequestMapping("/updatecarcommunication")
    public Object updateCarCommunication(@RequestParam("carcommunicationid") String carCommunicationId, @RequestBody CommunicationRecord communicationRecord) {
        return this.carCommunicationService.addRecord(carCommunicationId, communicationRecord);
    }

    @RequestMapping("/addinsurancecommunication")
    public Object addInsuranceCommunication(@RequestBody InsuranceCommunication insuranceCommunication){
        return this.insuranceCommunicationService.save(insuranceCommunication);
    }

    @RequestMapping("/updateinsurancecommunication")
    public Object updateInsuranceCommunication(@RequestParam("insurancecommunicationid") String insuranceCommunicationId, @RequestBody CommunicationRecord communicationRecord) {
        return this.insuranceCommunicationService.addRecord(insuranceCommunicationId, communicationRecord);
    }

    @RequestMapping("/carcommunicationallocate")
    public Object carCommunicationAllocate(@RequestParam("carcommunicationid") String carCommunicationId,@RequestBody Salesman salesman){
        return this.carCommunicationService.allocate(carCommunicationId,salesman);
    }

    @SchemaDocumentation("为保险回访单分配回访人员（业务员），以便进行回访")
    @GRequestMapping(path = "/insurancecommunicationallocate", method = RequestMethod.POST)
    @RequestMapping("/insurancecommunicationallocate")
    public InsuranceCommunication insuranceCommunicationAllocate(@RequestParam("insurancecommunicationid") String insuranceCommunicationId, @RequestParam("salesman") Salesman salesman){
        return this.insuranceCommunicationService.allocate(insuranceCommunicationId,salesman);
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
