package cn.zzk.validator.errors;

import java.util.List;

public class ValidException extends RuntimeException {

    private List<ValidSelectError> validSelectErrors;


    //todo : 还可以获取 method 信息

    //todo : 将message 设计为一个 json 对象
    public ValidException(List<ValidSelectError> errors, String message) {
        super(errors.toString());
        this.validSelectErrors = errors;

    }

    public List<ValidSelectError> getValidSelectErrors() {
        return validSelectErrors;
    }
}