package org.crygier.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.*;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * mutation方法调用到controller下的那些方法的各种元数据的封装
 */
public class DefaultMutationMetaInfo implements MutationMetaInfo {
    private IGraphQlTypeMapper graphQlTypeMapper;
    private Object target;
    private Method proxyMethod;//代理原本定义的method
    private String mutationFieldName;
    private GraphQLOutputType graphQLOutputType;
    private EntityType entityType;

    private Annotation[] methodAnnotations;
    private String[] argumentNames;
    private Map<String, Annotation[]> arguAnnotationsMap=new HashMap<>();
    private Map<String, GraphQLArgument> arguGraphQLArguments = new HashMap<>();

    private MutationValidator mutationValidator;

    public DefaultMutationMetaInfo(Object target, Method proxyMethod, IGraphQlTypeMapper graphQlTypeMapper, MutationValidator mutationValidator) {
        this.graphQlTypeMapper = graphQlTypeMapper;
        this.target = target;
        this.proxyMethod = proxyMethod;
        this.mutationValidator = mutationValidator;

        //一定能找到
        Class currentClass = proxyMethod.getDeclaringClass();
        while (!Object.class.equals(currentClass) && AnnotationUtils.getAnnotation(currentClass, GRestController.class) == null) {
            currentClass = currentClass.getSuperclass();
        }

        String grc = AnnotationUtils.findAnnotation(currentClass, GRestController.class).value();
        String grm = AnnotationUtils.findAnnotation(proxyMethod, GRequestMapping.class).path()[0];
        this.mutationFieldName = ("/" + grc + grm).replace("//", "/").replace("/", "_").substring(1);

        Method properMethod = ReflectionUtils.findMethod(currentClass, proxyMethod.getName(), proxyMethod.getParameterTypes());
        this.methodAnnotations = properMethod.getDeclaredAnnotations();
        boolean isCollectionReturnValue = false;


        Type type = properMethod.getReturnType();
        if (Collection.class.isAssignableFrom(properMethod.getReturnType()) && properMethod.getGenericReturnType() instanceof ParameterizedType) {
            type = ((ParameterizedType) properMethod.getGenericReturnType()).getActualTypeArguments()[0];
            isCollectionReturnValue = true;
        }
        this.entityType = this.graphQlTypeMapper.getEntityType((Class) type);
        this.graphQLOutputType = !isCollectionReturnValue ?graphQlTypeMapper.getGraphQLOutputType(type):
                entityType!=null?new GraphQLTypeReference(graphQlTypeMapper.getGraphQLTypeNameOfEntityList(this.entityType)): new GraphQLList(graphQlTypeMapper.getGraphQLOutputType(type));

        this.argumentNames = Arrays.stream(properMethod.getParameters())
                .map(parameter ->
                        {
                            String val = parameter.getAnnotation(RequestParam.class).value();
                            return StringUtils.hasText(val) ? val : parameter.getAnnotation(RequestParam.class).name();
                        }
                ).collect(Collectors.toList()).toArray(new String[]{});


        Map<RequestParam, Parameter> map =new HashMap<>();
        Arrays.stream(properMethod.getParameters())
                .forEach(parameter ->map.put(parameter.getAnnotation(RequestParam.class), parameter));
        Annotation[][] annotations = properMethod.getParameterAnnotations();
        for (int i = 0; i < argumentNames.length; i++) {
            this.arguAnnotationsMap.put(argumentNames[i], annotations[i]);
        }

        map.entrySet().stream().forEach(entry ->
        {
            RequestParam rp = entry.getKey();
            Parameter parameter = entry.getValue();
            String typeName = StringUtils.hasText(rp.value())?rp.value():rp.name();
            boolean required = rp.required();
            boolean isCollection = false;
            Class typeClazz = parameter.getType();
            if (parameter.isVarArgs()) {//参数如果为集合类型的，则必须找到最终的泛型类型
                isCollection = true;
                typeClazz = parameter.getType();
            } else if (Collection.class.isAssignableFrom(typeClazz)) {
                isCollection = true;
                Type[] actualTypeArguments = ((ParameterizedTypeImpl) parameter.getParameterizedType()).getActualTypeArguments();
                typeClazz = (Class) actualTypeArguments[0];
//                typeClazz = actualTypeArguments;
            }
            GraphQLInputType graphQLObjectInputType = this.graphQlTypeMapper.getGraphQLInputType(typeClazz);
            graphQLObjectInputType = isCollection ? new GraphQLList(graphQLObjectInputType) : graphQLObjectInputType;
            graphQLObjectInputType = required ? GraphQLNonNull.nonNull(graphQLObjectInputType) : graphQLObjectInputType;
            GraphQLArgument graphQLArgument = GraphQLArgument.newArgument()
                    .name(typeName)
                    .type(graphQLObjectInputType)
                    .build();
            this.arguGraphQLArguments.put(typeName, graphQLArgument);
        });

    }

    public Object invoke(Map<String, Object> nameArgMaps) {
        //默认值处理 TODO 这个要看validator和defautValue到底是谁先？我觉得应该是设置默认值优先，但需要分配给志铿去确认下。
        final Map<String, Object> argmaps = this.populateDefaultValueIfNecessary(nameArgMaps);
        //验证输入情况。
        try {
            if (this.mutationValidator != null) {
                this.mutationValidator.validate(this.getMutationMethodMetaInfo(argmaps));
            }
        } catch (MutationValidator.MutationValidateException mve) {
            throw new ValidateErrorException(mve);
        }
        //TODO 权限错误,业务逻辑错误，其他错误.
        Object[] args = Arrays.stream(this.argumentNames).map(argname -> argmaps.get(argname)).toArray();

        //TODO  如果input是数组  但是这里获取到的只是set集合  暂时是直接把类型改为Collection
//        if(nameArgMaps.containsKey("carbrandicon")){
//            args[0] = new ArrayList((Collection) args[0]);
//        }
        return ReflectionUtils.invokeMethod(this.proxyMethod, this.target, args);

    }

    /**
     * 有可能为null，如果不存在的话
     *
     * @param arguName
     * @return
     */
    public GraphQLArgument getGraphQLArgument(String arguName) {
        return this.arguGraphQLArguments.get(arguName);
    }

    @Override
    public GraphQLOutputType getGraphQLOutputType() {
        return this.graphQLOutputType;
    }

    @Override
    public List<GraphQLArgument> getGraphQLArgumentList() {
        return this.arguGraphQLArguments.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    @Override
    public EntityType getEntityType() {
        return this.entityType;
    }

    @Override
    public String getMutationFieldName() {
        return this.mutationFieldName;
    }

    protected Map<String, Object> populateDefaultValueIfNecessary(Map<String, Object> nameArgMaps) {
        //TODO 添加逻辑
        return nameArgMaps;
    }

    protected MutationValidator.MutationValidationMetaInfo getMutationMethodMetaInfo(Map<String, Object> nameArgMaps) {
        return new MutationValidator.MutationValidationMetaInfo() {

            @Override
            public Annotation[] getMethodAnnotations() {
                return DefaultMutationMetaInfo.this.methodAnnotations;
            }

            @Override
            public Annotation[] getArgumentAnnotations(String argumentName) {
                return DefaultMutationMetaInfo.this.arguAnnotationsMap.get(argumentName);
            }

            @Override
            public Object getArgumentObject(String argumentName) {
                return nameArgMaps.get(argumentName);
            }
        };

    }


    private final class ValidateErrorException extends RuntimeException implements GraphQLError

    {

        Map<String, Object> messageRelatePropsMap = new HashMap<>();

        public ValidateErrorException(MutationValidator.MutationValidateException mve) {
            super("输入数据错误");
            this.messageRelatePropsMap.putAll(mve.getMessageRelatePropsMap());
        }

        @Override
        public Map<String, Object> getExtensions() {
            return this.messageRelatePropsMap;
        }


        @Override
        public List<SourceLocation> getLocations() {
            return null;
        }

        @Override
        public ErrorType getErrorType() {
            return null;
        }

        @Override
        public List<Object> getPath() {
            return null;
        }

        @Override
        public Map<String, Object> toSpecification() {
            return null;
        }
    }


}




