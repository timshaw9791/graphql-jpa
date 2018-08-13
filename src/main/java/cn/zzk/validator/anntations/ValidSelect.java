package cn.zzk.validator.anntations;

import java.lang.annotation.*;

/**
 * 请求参数的组合逻辑校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidSelect {

    String value();

    String message() default "";

}