package cn.zzk.validator.exceptions;


import cn.zzk.validator.errors.ValidSelectError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 校验未通过时抛出的异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MutationValidateException extends RuntimeException {

    private ValidSelectError error;

    private String message;

    public MutationValidateException(ValidSelectError error) {
        super(error.toString());
        this.error = error;
        this.message = error.getMessage();
    }

    public ValidSelectError getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }
}