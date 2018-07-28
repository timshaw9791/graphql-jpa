package cn.zzk.validator.errors;

import java.util.List;

public class ValidSelectError {

    private String message = "方法校验未通过";

    private List<ParamError> errors;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ParamError> getErrors() {
        return errors;
    }

    public void setErrors(List<ParamError> errors) {
        this.errors = errors;
    }
}
