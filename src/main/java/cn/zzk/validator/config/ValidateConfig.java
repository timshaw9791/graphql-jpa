package cn.zzk.validator.config;

import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class ValidateConfig implements WebMvcConfigurer {

    @Override
    public Validator getValidator() {
        return new BosValidator();
    }
}