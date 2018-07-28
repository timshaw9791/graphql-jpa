package cn.zzk.validator.core;

import cn.zzk.validator.anntations.ValidSelect;
import cn.zzk.validator.errors.ParamError;
import cn.zzk.validator.errors.ParamInfo;
import cn.zzk.validator.errors.ValidException;
import cn.zzk.validator.errors.ValidSelectError;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Aspect
public class ValidateAspect {

    private static final ParameterNameDiscoverer parameterNameDiscoverer =
            new LocalVariableTableParameterNameDiscoverer();

    private static final ExecutableValidator methodValidator = BosValidatorFactory.getMethodValidator();


    //todo : 最后添加是否验证
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
            "&& @within(org.springframework.validation.annotation.Validated)")
    public void pointCut() {
    }


    @Before("pointCut()")
    private void valid(JoinPoint joinPoint) {
        Object[] paramValues = joinPoint.getArgs();
        Object target=joinPoint.getThis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        validate(target,method, paramValues,  methodValidator, parameterNameDiscoverer);
    }


    public static void validate(Object target,Method method, Object[] paramValues,  ExecutableValidator methodValidator, ParameterNameDiscoverer parameterNameDiscoverer) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        Set<ConstraintViolation<Object>> violations =
                methodValidator.validateParameters(target, method, paramValues);

        ValidSelect[] annotations = method.getAnnotationsByType(ValidSelect.class);

        List<ValidSelectError> selectErrorList;
        if (null == annotations || annotations.length == 0) {
            selectErrorList = withoutValidSelectStrategy(violations, parameterNames);
        } else {
            selectErrorList = hasValidSelectStrategy(violations, parameterNames, annotations);
        }

        if (!selectErrorList.isEmpty()) {
            throw new ValidException(selectErrorList, "方法校验未通过");
        }
    }

    /**
     * 存在 ValidSelect 注解的校验策略
     */
    private static List<ValidSelectError> hasValidSelectStrategy(Set<ConstraintViolation<Object>> violations,
                                                                 String[] parameterNames,
                                                                 ValidSelect[] selects) {
        List<ParamInfo> paramInfoList = new ArrayList<>(parameterNames.length);
        for (int i = 0; i < parameterNames.length; i++) {
            paramInfoList.add(new ParamInfo(parameterNames[i]));
        }

        violations.forEach(error -> {
            PathImpl path = (PathImpl) error.getPropertyPath();
            int paramIndex = path.getLeafNode().getParameterIndex();

            ParamInfo paramInfo = paramInfoList.get(paramIndex);
            paramInfo.setPass(false);
            paramInfo.setMessage(error.getMessage());
        });

        return Arrays.stream(selects)
                .map(select -> parseValidSelect(select, paramInfoList))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * 无 @ValidSelect 注解时的校验策略 -- 所有的都进行校验
     */
    private static List<ValidSelectError> withoutValidSelectStrategy(Set<ConstraintViolation<Object>> violations,
                                                                     String[] parameterNames) {
        List<ParamError> errors = violations
                .stream()
                .map(error -> {
                    PathImpl path = (PathImpl) error.getPropertyPath();

                    int paramIndex = path.getLeafNode().getParameterIndex();
                    String paramName = parameterNames[paramIndex];
                    String message = error.getMessage();

                    return new ParamError(paramName, message);

                }).collect(Collectors.toList());


        List<ValidSelectError> validSelectErrors = new ArrayList<>();
        if (!errors.isEmpty()) {
            ValidSelectError error = new ValidSelectError();
            error.setErrors(errors);
            validSelectErrors.add(error);
        }
        return validSelectErrors;
    }

    /**
     * 验证 @ValidSelect 的规则。
     *
     * @param validSelect controller 的 ValidSelect 注解的验证规则
     * @param infoList    校验方法的所有参数信息
     * @return 若校验通过，返回 Optional.empty();若校验不通过则返回不通过的信息
     */
    private static Optional<ValidSelectError> parseValidSelect(ValidSelect validSelect,
                                                               List<ParamInfo> infoList) {
        ValidSelectError validSelectError = new ValidSelectError();
        validSelectError.setMessage(validSelect.message());

        List<String> properties = ValidRuleParser.getProperties(validSelect.value());

        List<ParamError> errors = infoList.stream()
                .filter(info -> !info.isPass())
                .filter(info -> properties.contains(info.getParamName()))
                .map(info -> new ParamError(info.getParamName(), info.getMessage()))
                .collect(Collectors.toList());


        if (errors.isEmpty()) {
            return Optional.empty();
        } else {
            validSelectError.setErrors(errors);
            return Optional.of(validSelectError);
        }

    }


}
