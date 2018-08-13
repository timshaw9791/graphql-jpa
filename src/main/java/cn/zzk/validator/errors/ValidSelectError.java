package cn.zzk.validator.errors;

import java.util.List;

/**
 * 一个 @ValidSelect 的错误的校验结果。
 */
public class ValidSelectError {

    private String message;

    private List<ParamInfo> paramInfos;

    public String getMessage() {
        return message;
    }


    public List<ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public ValidSelectError(String message, List<ParamInfo> paramInfos) {
        this.message = message;
        this.paramInfos = paramInfos;
    }

}
