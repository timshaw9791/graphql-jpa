package cn.zzk.validator.anntations;


import cn.zzk.validator.core.DomainRuleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


/**
 * 该注解是为了在该注解的作用范围内(即 方法参数与 属性中)取代 @Valid 注解进行对复杂的对象构造复杂的校验语句。
 * <p>
 * 复杂的校验语句指的是对对象属性的分组校验。
 * hibernate validator 倾向于构造新的 Class 来完成分组，这会有以下的弊端:
 * 1.  校验组的构造较为繁琐
 * 2.  Controller 层的不同请求方法对同一对象的校验要求是不同的，这意味着要构造不同的校验组
 * <p>
 * 该注解为了解决以上两个问题，采取了 按需构造校验规则的方式 -- 校验规则的构造由 Value 完成
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DomainRuleValidator.class)
public @interface DomainRule {

    /**
     * 根据属性名书写校验规则，若为 ""则 @DomainRule 的作用等同于 @Valid注解
     * 例如要校验的类型:
     * <p>
     * User{
     *
     * @Length(min = 6,max = 24)
     * private String username;
     * @Email private String email;
     * @Length(min = 6,max = 24)
     * private String password;
     * <p>
     * }
     * </p>
     *
     * <p>
     * 例如要校验的方法:
     * void demo(@DomainRule(password && (email || username)) User user){}
     * <p>
     * 这意味着先对 @DomainRule 中的 property校验，然后校验逻辑判断
     */
    String value() default "";


    /**
     * 参数校验失败的信息
     */
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
