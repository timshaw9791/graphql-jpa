package cn.zzk.validator.errors;

public class ParamInfo {


    private boolean pass = true;


    private String paramName;

    private String message;


    public ParamInfo(String paramName) {
        this.paramName = paramName;
    }


    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }


    public String getParamName() {
        return paramName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
