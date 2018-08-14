package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@SchemaDocumentation("车辆来源相关的增删改操作")
@GRestController("mlshop")
@RestController
@CompileStatic
@Validated
public class BusinessController {
    //@SchemaDocumentation("GraphQlController.create测试下行不行")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{id}')")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{name,tel}')")
    //  @Validate(msg="一定要有姓名和id",value="exist('role{id,name}')")

    //1.赋值
    //2.验证
    //3.准备数据

    /*

    UserService{



        updatePassword(String id, String password){
            findByid(id).set(password);
        }

        @Assert("exist('client(id,number)')")
        createAcceptance(
        @Item("client")
        Client client){


        }
    }
*/


}