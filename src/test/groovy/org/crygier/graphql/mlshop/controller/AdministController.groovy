package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.Administ;
import org.crygier.graphql.mlshop.model.Insurance;
import org.crygier.graphql.mlshop.service.AdministService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/10 9:00
 */

@SchemaDocumentation("管理员")
@GRestController("mlshop")
@RestController
@CompileStatic
public class AdministController {

    @Autowired
    private AdministService administService;

    @SchemaDocumentation("增加管理员信息")
    @GRequestMapping(path = "/addadminist", method = RequestMethod.POST)
    Administ addAdminist(
            @RequestParam(name = "administ", required = true) Administ administ) {
        return administService.save(administ);
    }

    @SchemaDocumentation("修改管理员信息")
    @GRequestMapping(path = "/updateadminist", method = RequestMethod.POST)
    Administ updateAdminist(
            @RequestParam(name = "administ", required = true) Administ administ) {
        return administService.update(administ);
    }

    @SchemaDocumentation("修改管理员密码")
    @GRequestMapping(path = "/modifypassword", method = RequestMethod.POST)
    Administ modifyPassword(
            @RequestParam(name = "administ", required = true) Administ administ,
            @RequestParam(name = "password", required = true) String password) {
        return administService.modifyPassword(administ, password);
    }
}
