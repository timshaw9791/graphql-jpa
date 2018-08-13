package cn.zzk.validator.errors;


import cn.zzk.validator.exceptions.MutationValidateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 针对 MutationValidation 的处理。
 * 核心的是针对 MutationException 的处理。
 */
@ControllerAdvice
public class MutationValidationHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MutationValidateException.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, MutationValidateException ex) {
        ValidResponse response = new ValidResponse(ex.getError(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * 错误输出的匿名内部类
     */
    private static class ValidResponse {
        private String message;

        private int code;

        private List<ParamInfo> paramErrors;

        public ValidResponse(ValidSelectError validSelectError, int code) {
            this.paramErrors = validSelectError.getParamInfos();
            this.code = code;
            this.message = validSelectError.getMessage();
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }

        public Object getParamErrors() {
            return paramErrors;
        }
    }
}
