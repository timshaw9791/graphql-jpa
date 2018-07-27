package cn.zzk.test.controller;

import cn.zzk.test.domain.User;
import cn.zzk.validator.anntations.DomainRule;
import cn.zzk.validator.anntations.ValidSelect;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RequestMapping("/valid")
@RestController
@Validated
public class ValidController {


    @GetMapping("/test1")
    public void valid(@Length(min = 2, max = 10) String name,
                      @Min(18) int age) {
    }


    @GetMapping("/test2")
    public User test2(@DomainRule(value = "password && (email || phone)", message = "账号或密码错误") User user) {
        return user;
    }


    @ValidSelect( message = "注册校验错误",value = "user && (age || name)")
    @ValidSelect(value = "user && (age || name)", message = "注册校验错误")
    @GetMapping("/test3")
    public void test3(@DomainRule(value = "password && (email || phone)", message = "账号或密码错误") User user,
                      @Length(min = 2, max = 8) String name,
                      @Min(value=18) int age) {

    }


    @ValidSelect(value = "user", message = "用户注册错误")
    @ValidSelect(value = "name && age", message = "姓名或年龄错误")
    @GetMapping("/test4")
    public void test4(@DomainRule(value = "password && (email || phone)", message = "账号或密码错误") User user,
                      @Length(min = 2, max = 8) String name,
                      @Min(18 ) int age) {

    }

}
