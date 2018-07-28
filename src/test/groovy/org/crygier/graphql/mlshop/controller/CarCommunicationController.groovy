package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.CarCommunication
import org.crygier.graphql.mlshop.model.CommunicationRecord
import org.crygier.graphql.mlshop.service.CarCommunicationService
import org.crygier.graphql.mlshop.util.StringConvertUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/7/28 10:49
 */

@SchemaDocumentation("买车沟通")
@GRestController("mlshop")
@RestController
@CompileStatic
public class CarCommunicationController {
    @Autowired
    private CarCommunicationService carCommunicationService;

    @SchemaDocumentation("修改买车沟通信息")
    @GRequestMapping(path = "/addcarcommunicationrecord", method = RequestMethod.POST)
    CarCommunication updatecarCommunication(
            @RequestParam(name = "communicationrecord", required = true) CommunicationRecord communicationRecord,
            @RequestParam(name = "carcommunicationid", required = true) String carCommunicationId) {
        return this.carCommunicationService.addRecord(StringConvertUtil.getId(carCommunicationId),communicationRecord);
    }
}
