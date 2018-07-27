package cn.zzk.validator.config;

import cn.zzk.validator.core.ValidateAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class ValidationAutoConfig {

    @Bean
    public ValidateAspect validateAspect() {
        return new ValidateAspect();
    }

    @Bean
    @ConditionalOnBean(ValidateAspect.class)
    public ValidateConfig validateConfig() {
        System.out.println("自定义验证开启 .....................................");
        return new ValidateConfig();
    }
}
