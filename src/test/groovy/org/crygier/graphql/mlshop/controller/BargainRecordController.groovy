package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.BargainRecord
import org.crygier.graphql.mlshop.service.BargainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
/**
 * @author Curtain
 * @date 2018/8/27 10:27
 */

@SchemaDocumentation("砍价信息")
@GRestController("mlshop")
@RestController
@CompileStatic
class BargainRecordController {

    @Autowired
    private BargainService bargainService;

    @SchemaDocumentation("生成记录")
    @GRequestMapping(path = "/addbargainrecord")
    BargainRecord addBargainRecord(@RequestParam(name = "orderid", required = true) String orderId) {
        return this.bargainService.generate(orderId);
    }

    @SchemaDocumentation("砍价")
    @GRequestMapping(path = "/bargain")
    BargainRecord bargain(@RequestParam(name = "phone", required = true) String phone,
                          @RequestParam(name = "orderid", required = true) String orderId) {
        return this.bargainService.bargain(phone,orderId);
    }
}
