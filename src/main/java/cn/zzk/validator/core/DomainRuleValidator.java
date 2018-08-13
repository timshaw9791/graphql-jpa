package cn.zzk.validator.core;


import cn.zzk.validator.anntations.DomainRule;
import cn.zzk.validator.exceptions.DomainRuleCheckException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainRuleValidator implements ConstraintValidator<DomainRule, Object> {


    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    private String rule;


    @Override
    public void initialize(DomainRule domainRule) {
        rule = domainRule.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (RuleParser.isSimpleObject(value)) {
            String message = "@DomainRule 注解不能使用在简单类型及其包装类和String上";
            throw new DomainRuleCheckException(message);
        }
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        return validPass(rule, value, context);
    }

    private static boolean validPass(String rule, Object object, ConstraintValidatorContext context) {
        if ("".equals(rule)) {
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            return parseViolations(violations, context);
        } else {
            List<String> properties = RuleParser.getProperties(rule);
            BeanWrapper srcBean = new BeanWrapperImpl(object);

            List<Boolean> propertyValidResults = properties
                    .stream()
                    .map(property -> validProperty(property, srcBean, context))
                    .collect(Collectors.toList());

            return executeStringValid(rule, properties, propertyValidResults);
        }

    }


    /**
     * 对校验规则中的属性或参数 进行逻辑判断
     *
     * @param rule       校验规则
     * @param properties 要校验的属性
     * @param booleans   目标属性的boolean 值
     * @return 返回逻辑判断的结果
     */
    private static boolean executeStringValid(String rule, List<String> properties, List<Boolean> booleans) {
        String result = rule;
        for (int i = 0; i < properties.size(); i++) {
            result = result.replace(properties.get(i), booleans.get(i).toString());
        }

        return RuleParser.parser.parseExpression(result).getValue(boolean.class);
    }


    /**
     * 对验证未通过的属性收集并封装成 ConstraintViolation
     */
    private static boolean parseViolations(Set<ConstraintViolation<Object>> violations,
                                           ConstraintValidatorContext context) {
        if (violations.isEmpty()) {
            return true;
        } else {
            violations.forEach(violation -> {
                String name = violation.getPropertyPath().toString();
                context.buildConstraintViolationWithTemplate(violation.getMessage())
                        .addPropertyNode(name)
                        .addConstraintViolation();
            });
            return false;
        }

    }


    /**
     * 对复杂对象的属性进行验证
     *
     * @param property 要验证的属性
     * @param srcBean  复杂对象
     * @param context  验证失败的内容返回
     * @return 通过返回true, 不通过返回false
     */
    private static boolean validProperty(String property, BeanWrapper srcBean, ConstraintValidatorContext context) {
        Object propertyValue = srcBean.getPropertyValue(property);
        boolean isNull = propertyValue == null;

        if (isNull) {
            String name = property;
            context.buildConstraintViolationWithTemplate(property + " 不能为 null ")
                    .addPropertyNode(name)
                    .addConstraintViolation();
            return false;
        }
        Set<ConstraintViolation<Object>> violations =
                validator.validateProperty(srcBean.getWrappedInstance(), property);
        if (violations.isEmpty()) {
            if (RuleParser.isSimpleObject(propertyValue)) {
                violations = validator.validateProperty(srcBean.getWrappedInstance(), property);
            } else {
                violations = validator.validate(propertyValue);
            }
        }


        return parseViolations(violations, context);
    }

}
