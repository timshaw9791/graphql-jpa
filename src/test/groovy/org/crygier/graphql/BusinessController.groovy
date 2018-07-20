package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.CarSource
import org.crygier.graphql.mlshop.repo.CarSourceRepository;
import org.crygier.graphql.model.users.Role;
import org.crygier.graphql.model.users.User
import org.crygier.graphql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@SchemaDocumentation("车辆来源相关的增删改操作")
@GRestController("mlshop")
@RestController
@CompileStatic
public class BusinessController {


    @Autowired
    CarSourceRepository carSourceRepository;


    @SchemaDocumentation("增加车辆来源")
    @GRequestMapping(path = "/addcarsource", method = RequestMethod.POST)
    CarSource addcarsource(@RequestParam(name="carsource",required = true)CarSource client) {
        return this.carSourceRepository.save(client);
    }
    @SchemaDocumentation("修改车辆来源")
    @GRequestMapping(path = "/updatecarsource", method = RequestMethod.POST)
    CarSource updatecarsource(@RequestParam(name="carsource",required = true)CarSource client) {
        return this.carSourceRepository.save(client);
    }
    @SchemaDocumentation("禁用车辆来源")
    @GRequestMapping(path = "/disablecarsource", method = RequestMethod.POST)
    CarSource disablecarsource(@RequestParam(name="carsource",required = true)CarSource client) {
        client.disabled=true;
        return this.carSourceRepository.save(client);
    }


    @SchemaDocumentation("启用车辆来源")
    @GRequestMapping(path = "/enablcarsource", method = RequestMethod.POST)
    CarSource enablcarsource(@RequestParam(name="carsource",required = true)CarSource client) {
        client.disabled=false;
        return this.carSourceRepository.save(client);
    }





    //@SchemaDocumentation("GraphQlController.create测试下行不行")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{id}')")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{name,tel}')")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{id,name}')")

    //1.赋值
    //2.验证
    //3.准备数据

    /*

    UserService{



        updatePassword(String id, String pwd){
            findByid(id).set(pwd);
        }

        @Assert("exist('client(id,number)')")
        createAcceptance(
        @Item("client")
        Client client){


        }
    }
*/



}