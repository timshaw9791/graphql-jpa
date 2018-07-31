package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.model.CommunicationRecord;
import org.crygier.graphql.mlshop.service.CarCommunicationService;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author Curtain
 * @date 2018/7/31 10:29
 */
@RestController
@CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS},maxAge=1800L,allowedHeaders ="*")
public class Controller {

    @Autowired
    private CarInfoService service;

    @Autowired
    private CarCommunicationService carCommunicationService;

    @RequestMapping("/updatecarinfo")
    public Object updateCarInfo(@RequestBody CarInfo carInfo){
        return service.update(carInfo);
    }

    @RequestMapping("/addcarcommunication")
    public Object addCarCommunication(@RequestBody CarCommunication carCommunication){
        return this.carCommunicationService.save(carCommunication);
    }

    @RequestMapping("/updatecarcommunication")
    public Object updateCarCommunication(@RequestParam("carcommunicationid") String carCommunicationId,@RequestBody CommunicationRecord communicationRecord){
        return this.carCommunicationService.addRecord(carCommunicationId,communicationRecord);
    }
}
