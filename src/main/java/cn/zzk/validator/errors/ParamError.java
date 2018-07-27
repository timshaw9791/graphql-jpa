package cn.zzk.validator.errors;

/**
 * 包装参数的验证信息
 */
public class ParamError {

    private String paramName;

    private String message;

    public ParamError(String paramName, String message) {
        this.paramName = paramName;
        this.message = message;
    }

    public String getParamName() {
        return paramName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "paramName : " + paramName + " , " +
                "message : " + message;
    }
}
