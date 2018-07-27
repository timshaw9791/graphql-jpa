package cn.zzk.validator.core;

import cn.zzk.validator.anntations.DomainRule;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

public class DomainRuleValidator implements ConstraintValidator<DomainRule, Object> {


    private static final Validator validator = BosValidatorFactory.getValidator();


    private String rule;


    @Override
    public void initialize(DomainRule domainRule) {
        rule = domainRule.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return validPass(rule, value);
    }

    /**
     * 根据 rule 校验目标对象
     * rule 为空 ，则默认根据 domain 的 jsr303 校验规则校验
     *
     * @param rule   校验规则
     * @param object 要校验的目标对象
     * @return true则通过校验，false 则校验失败
     */
    private static boolean validPass(String rule, Object object) {

        if ("".equals(rule)) {
            return validator.validate(object).isEmpty();
        }
        List<String> properties = cn.zzk.validator.core.ValidRuleParser.getProperties(rule);
        BeanWrapper srcBean = new BeanWrapperImpl(object);

        List<Boolean> propertyValidResults = properties.stream()
                .map(property -> {
                    boolean notNull = srcBean.getPropertyValue(property) != null;
                    boolean valid = validator.validateProperty(object, property).isEmpty();
                    return notNull && valid;
                })
                .collect(Collectors.toList());

        return ValidRuleParser.executeStringValid(rule, properties, propertyValidResults);

    }


}
