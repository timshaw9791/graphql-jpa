package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.mlshop.model.*;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService;
import org.crygier.graphql.mlshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/7/31 10:29
 */
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
    private InsuranceCommunicationService insuranceCommunicationService;

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

    @RequestMapping("/addorder")
    public Order order(@RequestBody Order order) {
        return orderService.save(order);
    }
}
