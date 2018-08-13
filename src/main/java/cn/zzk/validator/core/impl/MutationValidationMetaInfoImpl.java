package cn.zzk.validator.core.impl;


import cn.zzk.validator.core.MutationValidationMetaInfo;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

public class MutationValidationMetaInfoImpl implements MutationValidationMetaInfo {

    private static final ParameterNameDiscoverer discoverer =
            new DefaultParameterNameDiscoverer();

    private Method method;


    private Object[] argumentObjects;

    private String[] argumentNames;

    private Object target;


    public MutationValidationMetaInfoImpl(Method method, Object[] argumentObjects, Object target) {
        this.method = method;
        this.argumentObjects = argumentObjects;
        this.target = target;

        argumentNames = discoverer.getParameterNames(method);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String[] getArgumentNames() {
        return argumentNames;
    }


    @Override
    public Object[] getArgumentObjects() {
        return argumentObjects;
    }

    @Override
    public Object getTarget() {
        return target;
    }


}
