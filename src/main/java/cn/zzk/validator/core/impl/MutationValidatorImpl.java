package cn.zzk.validator.core.impl;


import cn.zzk.validator.anntations.DomainRule;
import cn.zzk.validator.anntations.ValidSelect;
import cn.zzk.validator.core.MutationValidationMetaInfo;
import cn.zzk.validator.core.MutationValidator;
import cn.zzk.validator.core.RuleParser;
import cn.zzk.validator.errors.ParamInfo;
import cn.zzk.validator.errors.ValidSelectError;
import cn.zzk.validator.exceptions.MutationValidateException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MutationValidatorImpl implements MutationValidator, Validator {

    private static final ExecutableValidator executableValidator =
            Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

    private static final String VALID_SELECT_DEFAULT_MESSAGE = "请求参数校验未通过";

    @Override
    public void validate(MutationValidationMetaInfo metaInfo) {

        Method method = metaInfo.getMethod();
        Object[] paramValues = metaInfo.getArgumentObjects();
        String[] parameterNames = metaInfo.getArgumentNames();
        Object target = metaInfo.getTarget();


        ValidSelect validSelect = method.getAnnotation(ValidSelect.class);

        List<ParamInfo> paramInfoList = initParamInfo(target, method, paramValues, parameterNames);

        Optional<ValidSelectError> error;
        if (null == validSelect) {
            error = withoutValidSelectStrategy(paramInfoList);
        } else {
            error = hasValidSelectStrategy(paramInfoList, validSelect);
        }
        if (error.isPresent())
            throw new MutationValidateException(error.get());
    }

    /**
     * 没有 @ValidSelect 的校验策略。
     * 实质是生成一个 ValidSelectError
     */
    private static Optional<ValidSelectError> withoutValidSelectStrategy(List<ParamInfo> paramInfoList) {
        paramInfoList.removeIf(ParamInfo::isPass);
        if (paramInfoList.isEmpty()) {
            return Optional.empty();

        }
        ValidSelectError error = new ValidSelectError(VALID_SELECT_DEFAULT_MESSAGE, paramInfoList);
        return Optional.of(error);
    }

    /**
     * 有 @ValidSelect 的校验策略。
     * 每一个 @ValidSelect 注解对应一个 ValidSelectError
     */
    private static Optional<ValidSelectError> hasValidSelectStrategy(List<ParamInfo> paramInfoList,
                                                                     ValidSelect validSelect) {
        return parseValidSelect(validSelect, paramInfoList);
    }


    /**
     * 在方法校验之后会封装 paramInfo
     */
    private static List<ParamInfo> initParamInfo(Object target,
                                                 Method method,
                                                 Object[] paramValues,
                                                 String[] parameterNames) {

        Set<ConstraintViolation<Object>> violations =
                executableValidator.validateParameters(target, method, paramValues);

        List<ParamInfo> paramInfos = new ArrayList<>(parameterNames.length);
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter parameter = method.getParameters()[i];

            String message = parameterNames[i] + " 格式校验未通过";
            if (parameter.isAnnotationPresent(DomainRule.class)) {
                String ruleMessage = parameter.getAnnotation(DomainRule.class).message();
                if (!"".equals(ruleMessage)) {
                    message = ruleMessage;
                }
            }
            paramInfos.add(new ParamInfo(parameterNames[i], paramValues[i], message));
        }

        violations.forEach(violation -> {
            String message = violation.getMessage();
            PathImpl path = (PathImpl) violation.getPropertyPath();
            int paramIndex = 0;

            if (path.getLeafNode().getKind() == ElementKind.PARAMETER) {
                paramIndex = path.getLeafNode().getParameterIndex();
                paramInfos.get(paramIndex).addError(message);

            } else {
                StringBuilder fieldBuilder = new StringBuilder();
                while (path.getLeafNode().getKind() != ElementKind.PARAMETER) {
                    fieldBuilder.insert(0, path.getLeafNode().getName())
                            .insert(0, ".");

                    path.removeLeafNode();

                }
                fieldBuilder.insert(0, parameterNames[paramIndex]);
                paramIndex = path.getLeafNode().getParameterIndex();

                paramInfos.get(paramIndex).addError(fieldBuilder.toString(), message);
            }

        });
        return paramInfos;
    }

    /**
     * 根据@ValidSelect 与 errors 来包装 ValidSelectError
     *
     * @param validSelect 指定的 validSelect 对象
     * @param errors      包装信息
     * @return 若校验通过，则包装信息为 Optional.empty(),否则会有校验错误对象 -- ValidSelectError
     */
    private static Optional<ValidSelectError> parseValidSelect(ValidSelect validSelect,
                                                               List<ParamInfo> errors) {


        List<String> properties = RuleParser.getProperties(validSelect.value());

        List<ParamInfo> paramInfoList = errors.stream()
                .filter(info -> !info.isPass())
                .filter(info -> properties.contains(info.getParamName()))
                .collect(Collectors.toList());


        if (paramInfoList.isEmpty()) {
            return Optional.empty();
        } else {
            String message = validSelect.message().equals("") ?
                    VALID_SELECT_DEFAULT_MESSAGE : validSelect.message();
            ValidSelectError validSelectError = new ValidSelectError(message, paramInfoList);
            return Optional.of(validSelectError);
        }

    }


    @Override
    public boolean supports(Class<?> clazz) {
        // nothing to do !
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        // nothing to do !
    }
}
