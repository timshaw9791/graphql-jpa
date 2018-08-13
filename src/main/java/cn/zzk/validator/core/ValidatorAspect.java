package cn.zzk.validator.core;


import cn.zzk.validator.core.impl.MutationValidationMetaInfoImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Component
@Aspect
public class ValidatorAspect {

    private MutationValidator mutationValidator;

    public ValidatorAspect(MutationValidator mutationValidator) {
        this.mutationValidator = mutationValidator;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" +
            "&& @within(org.springframework.validation.annotation.Validated)")
    public void pointCut() {
    }


    @Before("pointCut()")
    private void valid(JoinPoint joinPoint) {
        Object obj = joinPoint.getThis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        MutationValidationMetaInfo info = new MutationValidationMetaInfoImpl(method, args, obj);
        mutationValidator.validate(info);
    }


}
