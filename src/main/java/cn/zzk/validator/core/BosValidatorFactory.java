package cn.zzk.validator.core;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

public class BosValidatorFactory {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    private static final ExecutableValidator methodValidator = factory.getValidator().forExecutables();


    public static Validator getValidator() {
        return validator;
    }

    public static ExecutableValidator getMethodValidator() {
        return methodValidator;
    }

    private BosValidatorFactory() { }
}
