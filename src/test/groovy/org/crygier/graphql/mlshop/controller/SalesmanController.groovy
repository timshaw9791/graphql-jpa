package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.anntations.Exclude;
import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.repo.SalesmanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/14 8:55
 */
@SchemaDocumentation("业务员信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class SalesmanController {


    @Autowired
    private SalesmanRepository salesmanRepository;

    @SchemaDocumentation("增加业务员")
    @GRequestMapping(path = "/addsalesman", method = RequestMethod.POST)
    Salesman addSalesman(@RequestParam(name = "salesman", required = true) Salesman salesman) {
        return this.salesmanRepository.save(salesman);
    }

    @SchemaDocumentation("修改业务员")
    @Exclude
    @GRequestMapping(path = "/updatesalesman", method = RequestMethod.POST)
    Salesman updateSalesman(@RequestParam(name = "salesman", required = true) Salesman salesman) {
        return this.salesmanRepository.save(salesman);
    }

    @SchemaDocumentation("禁用业务员")
    @GRequestMapping(path = "/disablesalesman", method = RequestMethod.POST)
    Salesman disableSalesman(@RequestParam(name = "salesman", required = true) Salesman salesman) {
        salesman.setDisabled(true);
        return this.salesmanRepository.save(salesman);
    }

    @SchemaDocumentation("启用业务员")
    @GRequestMapping(path = "/enablsalesman", method = RequestMethod.POST)
    Salesman enableSalesman(@RequestParam(name = "salesman", required = true) Salesman salesman) {
        salesman.setDisabled(false);
        return this.salesmanRepository.save(salesman);
    }

    @SchemaDocumentation("删除业务员")
    @GRequestMapping(path = "/removesalesman", method = RequestMethod.POST)
    Salesman removeSalesman(@RequestParam(name = "salesman", required = true) Salesman salesman) {
        salesman.setDisabled(true);
        return this.salesmanRepository.save(salesman);
    }


}
