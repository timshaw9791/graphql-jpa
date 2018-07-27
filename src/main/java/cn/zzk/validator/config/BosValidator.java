package cn.zzk.validator.config;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 该类的目的是为了替换 spring boot 提供的默认的验证器，实际的验证器是 ValidateAspect
 *
 * @author zzk
 * @date 2018/6/20
 */
public final class BosValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}