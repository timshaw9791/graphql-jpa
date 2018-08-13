package cn.zzk.validator.errors;


/**
 * 对复杂的校验错误的对象进行封装
 *
 */
public class FieldError {

    private String field;

    private String message;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "field : " + field + " , " +
                "message : " + message;
    }
}
