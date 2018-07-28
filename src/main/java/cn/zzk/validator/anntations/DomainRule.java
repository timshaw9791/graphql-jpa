package cn.zzk.validator.anntations;


import cn.zzk.validator.core.DomainRuleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DomainRuleValidator.class)
public @interface DomainRule {

    String value() default "";

    String message();


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
