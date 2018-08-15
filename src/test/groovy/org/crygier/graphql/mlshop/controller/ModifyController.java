package org.crygier.graphql.mlshop.controller;

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

    @RequestMapping("/insurancecommunicationallocate")
    public Object insuranceCommunicationAllocate(@RequestParam("insurancecommunicationid") String insuranceCommunicationId,@RequestBody Salesman salesman){
        return this.insuranceCommunicationService.allocate(insuranceCommunicationId,salesman);
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
