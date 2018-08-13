package cn.zzk.validator.core;


import java.lang.reflect.Method;

/**
 * 对 MutationValidation 校验的准备数据的封装
 */
public interface MutationValidationMetaInfo {


    /**
     * 获得当前校验方法的所有实际参数值
     */
    Object[] getArgumentObjects();


    /**
     * 获得要校验的方法的对象实例
     */
    Object getTarget();

    /**
     * 返回校验的目标方法.
     * 该方法是校验器 MutationValidator 校验的目标对象
     *
     * @return 返回校验器校验的目标方法
     */
    Method getMethod();

    /**
     * 获得参数名数组
     */
    String[] getArgumentNames();


}
