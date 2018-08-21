package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude
import org.crygier.graphql.mlshop.model.CommunicationRecord
import org.crygier.graphql.mlshop.model.Insurance
import org.crygier.graphql.mlshop.model.InsuranceCommunication
import org.crygier.graphql.mlshop.model.Salesman
import org.crygier.graphql.mlshop.service.InsuranceCommunicationService
import org.crygier.graphql.mlshop.service.InsuranceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
/**
 * @author Curtain
 * @date 2018/7/28 10:53
 */

@SchemaDocumentation("保险信息")
@GRestController("mlshop")
@RestController
@CompileStatic
class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private InsuranceCommunicationService insuranceCommunicationService;


    @SchemaDocumentation("增加保险信息")
    @GRequestMapping(path = "/addinsurance", method = RequestMethod.POST)
    Insurance addInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        return insuranceService.save(insurance);
    }

    @SchemaDocumentation("修改保险信息")
    @Exclude
    @GRequestMapping(path = "/updateinsurance", method = RequestMethod.POST)
    Insurance updateInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        return insuranceService.update(insurance);
    }

    @SchemaDocumentation("删除保险信息")
    @Exclude
    @GRequestMapping(path = "/removeinsurance", method = RequestMethod.POST)
    Insurance removeInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        insuranceService.deleteById(insurance);
        return insurance;
    }

    @SchemaDocumentation("增加保险回访记录")
    @GRequestMapping(path = "/addinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication addInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        return insuranceCommunicationService.save(insuranceCommunication);
    }

    @SchemaDocumentation("修改保险回访记录")
    @Exclude
    @GRequestMapping(path = "/updateinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication updateInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        return this.insuranceCommunicationService.save(insuranceCommunication);
    }


    @SchemaDocumentation("删除保险回访记录")
    @Exclude
    @GRequestMapping(path = "/removeinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication removeInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        this.insuranceCommunicationService.deleteById(insuranceCommunication);
        return insuranceCommunication;
    }

    @SchemaDocumentation("为保险回访单分配回访人员（业务员），以便进行回访")
    @GRequestMapping(path = "/insurancecommunicationallocate", method = RequestMethod.POST)
    public InsuranceCommunication insuranceCommunicationAllocate(@RequestParam("insurancecommunicationid") String insuranceCommunicationId, @RequestParam("salesman") Salesman salesman){
        return this.insuranceCommunicationService.allocate(insuranceCommunicationId,salesman);
    }

    @SchemaDocumentation("添加回访记录")
    @GRequestMapping("/addinsurancecommunicationrecord")
    public InsuranceCommunication updateInsuranceCommunication(@RequestParam("insurancecommunicationid") String insuranceCommunicationId, @RequestParam("record") CommunicationRecord communicationRecord) {
        return this.insuranceCommunicationService.addRecord(insuranceCommunicationId, communicationRecord);
    }


}
