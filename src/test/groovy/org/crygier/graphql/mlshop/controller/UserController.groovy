package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.user.User
import org.crygier.graphql.mlshop.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
/**
 * @author Curtain
 * @date 2018/8/15 15:14
 */

@SchemaDocumentation("用户信息")
@GRestController("mlshop")
@RestController
@CompileStatic
class UserController {

    @Autowired
    private UserService userService;

    @SchemaDocumentation("用户注册")
    @GRequestMapping(path = "/registeruser", method = RequestMethod.POST)
    User registerUser(@RequestParam(name = "user", required = true) User user) {
        return userService.register(user);
    }

    @SchemaDocumentation("修改密码")
    @GRequestMapping(path = "/modifypassword", method = RequestMethod.POST)
    String modifyPassword(@RequestParam(name = "password", required = true) String password,
                          @RequestParam(name = "id", required = true) String id) {
        userService.modifyPassword(password, id);
        return "成功";
    }
    @SchemaDocumentation("修改手机号")
    @GRequestMapping(path = "/modifyphone", method = RequestMethod.POST)
    String modifyPhone(@RequestParam(name = "pbone", required = true) String phone,
                          @RequestParam(name = "id", required = true) String id) {
        userService.modifyPhone(phone, id);
        return "成功";
    }

    @SchemaDocumentation("修改信息")
    @GRequestMapping(path = "/updateuser", method = RequestMethod.POST)
    User updateUser(@RequestParam(name = "user", required = true) User user,
                       @RequestParam(name = "id", required = true) String id) {
        return userService.update(user, id);
    }
}
