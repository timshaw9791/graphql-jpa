package cn.zzk.validator.exceptions;

/**
 * DomainRule 中的 rule 不符合规范，则抛出此异常
 */
public class DomainRuleCheckException extends RuntimeException {

    public DomainRuleCheckException(String message) {
        super(message);
    }
}
