package org.crygier.graphql.mlshop.controller;


import cn.zzk.test.domain.Person;
import cn.zzk.test.domain.User;
import cn.zzk.validator.anntations.DomainRule;
import cn.zzk.validator.anntations.ValidSelect;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RequestMapping("/valid")
@RestController
@Validated
public class ValidController {


    @GetMapping("/test1")
    public void valid(@Length(min = 2, max = 10) String name,
                      @Min(18) int age) {
    }


    @PostMapping("/test2")
    public User test2(@DomainRule @RequestBody User user) {
        return user;
    }

    @ValidSelect(value = "test && age", message = "需要通过18岁")
    @GetMapping("/test3")
    public void test3(@Range(min = 0, max = 15) int test,
                      @Length(min = 2, max = 8) String name,
                      @Min(18) int age) {
        System.out.println(name);
    }


    @ValidSelect(value = "user && name && age", message = "用户名或密码错误")
    @GetMapping("/test4")
    public void test4(@DomainRule(value = "password && (email || phone)", message = "用户名或密码错误") User user,
                      @Length(min = 2, max = 8) String name,
                      @Min(18) int age) {

    }

    @PostMapping("/test5")
    public Person test5(@DomainRule(value = "fullName", message = "person 校验未通过") @RequestBody Person person) {
        return person;
    }

}
