package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude;
import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/14 9:03
 */
@SchemaDocumentation("业务员信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @SchemaDocumentation("增加用户信息")
    @GRequestMapping(path = "/addcustomer", method = RequestMethod.POST)
    Customer addcustomer(@RequestParam(name = "customer", required = true) Customer client) {
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("修改用户信息")
    @Exclude
    @GRequestMapping(path = "/updatecustomer", method = RequestMethod.POST)
    Customer updatecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("禁用用户信息")
    @Exclude
    @GRequestMapping(path = "/disablecustomer", method = RequestMethod.POST)
    Customer disablecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.setDisabled(true);
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("启用用户信息")
    @Exclude
    @GRequestMapping(path = "/enablcustomer", method = RequestMethod.POST)
    Customer enablcustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.setDisabled(false);
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("删除用户信息")
    @Exclude
    @GRequestMapping(path = "/removecustomer", method = RequestMethod.POST)
    Customer removecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.setDisabled(false);
        return this.customerRepository.save(client);
    }
}
