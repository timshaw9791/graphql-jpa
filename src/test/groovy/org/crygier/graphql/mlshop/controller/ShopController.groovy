package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude
import org.crygier.graphql.mlshop.model.Shop
import org.crygier.graphql.mlshop.repo.ShopRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Curtain
 * @date 2018/8/10 9:33
 */

@SchemaDocumentation("门店信息")
@GRestController("mlshop")
@RestController
@CompileStatic
class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @SchemaDocumentation("增加门店")
    @GRequestMapping(path = "/addshop", method = RequestMethod.POST)
    Shop addshop(@RequestParam(name = "shop", required = true) Shop client) {
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("修改门店")
    @Exclude
    @GRequestMapping(path = "/updateshop", method = RequestMethod.POST)
    //@Include("createtime","updatetime"...)
    Shop updateshop(@RequestParam(name = "shop", required = true) Shop client) {
        //通过aop 获取到service 或者 也可以是 repository   然后查找数据   findone(client.getid)
        //根据指定的字段 进行覆盖 （如这里注解指定的createtime..）
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("禁用门店")
    @GRequestMapping(path = "/disableshop", method = RequestMethod.POST)
    Shop disableshop(@RequestParam(name = "shop", required = true) Shop client) {
        client.disabled = true;
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("启用门店")
    @GRequestMapping(path = "/enablshop", method = RequestMethod.POST)
    Shop enablshop(@RequestParam(name = "shop", required = true) Shop client) {
        client.disabled = false;
        return this.shopRepository.save(client);
    }

//    @SchemaDocumentation("删除门店")
//    @GRequestMapping(path = "/removeshop", method = RequestMethod.POST)
//    Shop removeshop(@RequestParam(name = "shop", required = true) Shop client) {
//        client.disabled = false;
//        this.shopRepository.deleteById(client.getId());
//        return client;
//    }
}
