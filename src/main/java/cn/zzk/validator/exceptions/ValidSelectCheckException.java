package cn.zzk.validator.exceptions;

/**
 * ValidSelect 的检查异常.
 * 若 ValidSelect 中的 rule 不符合规范，则抛出此一次
 */
public class ValidSelectCheckException extends RuntimeException {

    public ValidSelectCheckException(String message) {
        super(message);

    }
}
