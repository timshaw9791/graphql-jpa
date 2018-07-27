package cn.zzk.validator.anntations;

import cn.zzk.validator.anntations.ValidSelect.List;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//todo : 最后转换成校验注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(List.class)
public @interface ValidSelect {

    String value();

    String message();


    @Target(METHOD)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidSelect[] value();
    }

}