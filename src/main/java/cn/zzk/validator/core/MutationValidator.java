package cn.zzk.validator.core;


import cn.zzk.validator.exceptions.MutationValidateException;

/**
 * RestController 的方法校验器
 */
public interface MutationValidator {

    /**
     * 对 metaInfo 实例进行校验，校验不通过则抛出 MutationValidateException 异常。
     *
     * @param metaInfo 一个校验实例,一个对象的方法的实例的包装
     * @throws MutationValidateException 校验失败时抛出的异常
     */
    void validate(MutationValidationMetaInfo metaInfo) throws MutationValidateException;
}
