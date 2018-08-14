package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.CarSource;
import org.crygier.graphql.mlshop.repo.CarSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/14 8:51
 */

@SchemaDocumentation("车辆来源信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class CarSourceController {

    @Autowired
    CarSourceRepository carSourceRepository;


    @SchemaDocumentation("增加车辆来源")
    @GRequestMapping(path = "/addcarsource", method = RequestMethod.POST)
    CarSource addCarSource(@RequestParam(name = "carsource", required = true) CarSource carSource) {
        return this.carSourceRepository.save(carSource);
    }

    @SchemaDocumentation("修改车辆来源")
    @GRequestMapping(path = "/updatecarsource", method = RequestMethod.POST)
    CarSource updateCarSource(@RequestParam(name = "carsource", required = true) CarSource carSource) {
        return this.carSourceRepository.save(carSource);
    }

    @SchemaDocumentation("禁用车辆来源")
    @GRequestMapping(path = "/disablecarsource", method = RequestMethod.POST)
    CarSource disableCarSource(@RequestParam(name = "carsource", required = true) CarSource carSource) {
        carSource.setDisabled(true);
        return this.carSourceRepository.save(carSource);
    }

    @SchemaDocumentation("启用车辆来源")
    @GRequestMapping(path = "/enablcarsource", method = RequestMethod.POST)
    CarSource enableCarSource(@RequestParam(name = "carsource", required = true) CarSource carSource) {
        carSource.setDisabled(false);
        return this.carSourceRepository.save(carSource);
    }

    @SchemaDocumentation("删除车辆来源")
    @GRequestMapping(path = "/removecarsource", method = RequestMethod.POST)
    CarSource removeCarSource(@RequestParam(name = "carsource", required = true) CarSource carSource) {
        carSource.setDisabled(true);
        return this.carSourceRepository.save(carSource);
    }
}
