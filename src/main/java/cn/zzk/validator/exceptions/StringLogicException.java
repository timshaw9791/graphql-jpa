package cn.zzk.validator.exceptions;

/**
 * 若字符串逻辑不能转换成 boolean ，则抛出此异常.
 * 例如 字符串逻辑 "true true" 不能转换成 true 或者 false
 */
public class StringLogicException extends RuntimeException {

    public StringLogicException(String message) {
        super(message);
    }
}
