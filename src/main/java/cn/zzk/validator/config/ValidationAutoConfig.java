package cn.zzk.validator.config;


import cn.zzk.validator.core.MutationValidator;
import cn.zzk.validator.core.ValidatorAspect;
import cn.zzk.validator.core.impl.MutationValidatorImpl;
import cn.zzk.validator.errors.MutationValidationHandler;
import cn.zzk.validator.events.RuleValidCheckEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class ValidationAutoConfig {

    @Bean
    public ValidatorAspect validateAspect() {
        return new ValidatorAspect(mutationValidator());
    }

    @Bean
    public MutationValidator mutationValidator() {
        return new MutationValidatorImpl();
    }

    @Bean
    public RuleValidCheckEvent ruleValidEvent() {
        return new RuleValidCheckEvent();
    }

    @Bean
    public MutationValidationHandler controllerValidationHandler() {
        return new MutationValidationHandler();
    }

}
