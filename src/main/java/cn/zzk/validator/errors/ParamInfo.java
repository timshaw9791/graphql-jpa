package cn.zzk.validator.errors;


import cn.zzk.validator.core.RuleParser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 对方法验证未通过的参数的信息的包装。
 */
public class ParamInfo {

    @JsonIgnore
    private boolean pass = true;

    /**
     * 该参数的值是否是简单对象
     */
    private boolean isSimpleObject = true;

    /**
     * 参数的实际名称
     */
    private String paramName;

    /**
     * 参数的实际值
     */
    private Object paramValue;


    /**
     * 参数的错误信息
     */
    private String message;


    /**
     * 若 paramValue 不是简单对象，则 该属性存在实例
     */
    private List<FieldError> fieldErrors;


    public ParamInfo(String paramName, Object paramValue, String message) {
        this.paramName = paramName;
        this.paramValue = paramValue;

        if (!RuleParser.isSimpleObject(paramValue)) {
            isSimpleObject = false;
            fieldErrors = new ArrayList<>();
            this.message = message;
        }

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPass() {
        return pass;
    }


    public String getParamName() {
        return paramName;
    }

    public void addError(String field, String message) {
        pass = false;
        fieldErrors.add(new FieldError(field, message));
    }

    public void addError(String message) {
        pass = false;
        this.message = message;
    }

    /**
     * @return 返回错误的属性信息
     */
    @JsonIgnore
    public Optional<List<FieldError>> getFieldErrorsList() {
        return Optional.ofNullable(fieldErrors);
    }

    /**
     * @return 默认通过 Map 输出属性的错误信息
     */
    public Optional<Map<String, String>> getFieldErrors() {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            return Optional.empty();
        }
        Map<String, String> map = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getMessage));
        return Optional.of(map);
    }

    public Object getParamValue() {
        return paramValue;
    }


    public boolean isSimpleObject() {
        return isSimpleObject;
    }
}
