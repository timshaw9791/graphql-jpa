package cn.zzk.test.handle;

import cn.zzk.validator.errors.ValidException;
import cn.zzk.validator.errors.ValidSelectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ValidationHandler {


    //todo : 暂时测试用的异常处理
    @ExceptionHandler(ValidException.class)
    public List<ValidSelectError> handleValidationException(ValidException e) {
        return e.getValidSelectErrors();
    }
}
