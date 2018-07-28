package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.IEntity;
import cn.zzk.validator.core.ValidateAspect;
import cn.zzk.validator.errors.ValidException;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.*;
import graphql.schema.*;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.validation.Validation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MutationDataFetcher extends CollectionJpaDataFetcher {

    MutationMethodMetaInfo mutationMethodMetaInfo = null;

    public MutationDataFetcher(EntityManager entityManager, EntityType entityType, MutationMethodMetaInfo mutationMethodMetaInfo, IGraphQlTypeMapper graphQlTypeMapper) {
        super(entityManager, entityType, graphQlTypeMapper);
        this.mutationMethodMetaInfo = mutationMethodMetaInfo;

    }


    @Override
    public Object getResult(DataFetchingEnvironment environment) {
        Field field = environment.getFields().iterator().next();
        //TODO get mutation name,argument type.
        // field.getArguments().stream()

        Object[] realArguments = new Object[this.mutationMethodMetaInfo.getGqalist().size()];
        Object returnValue = null;

        field.getArguments().forEach(realArg ->
        {
            IntStream.range(0, this.mutationMethodMetaInfo.getGqalist().size())
                    .forEach(idx -> {
                        if (this.mutationMethodMetaInfo.getGqalist().get(idx).getName().equals(realArg.getName())) {
                            GraphQLType graphQLType = this.mutationMethodMetaInfo.getGqalist().get(idx).getArgument().getType();
                            try {
                                realArguments[idx] = convertValue(environment, (GraphQLInputType) graphQLType, realArg.getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("getArguments erroros!");
                            }
                        }
                    });
        });

        //异常处理 TODO，输入检查错误,权限错误,业务逻辑错误，其他错误.

        validate(this.mutationMethodMetaInfo, realArguments);
        returnValue = ReflectionUtils.invokeMethod(this.mutationMethodMetaInfo.getProperMethod(), this.mutationMethodMetaInfo.getTarget(),realArguments);

        //说明返回的是简单类型
        if (this.entityType == null) {
            return returnValue;
        } else if (returnValue == null) {
            return null;
        } else {


            if (IEntity.class.isAssignableFrom(returnValue.getClass())) {//拿到主键
                String id = ((IEntity) returnValue).getId();
                environment.getField().getArguments().clear();
                environment.getField().getArguments().add(new Argument("id", new StringValue(id)));
                return super.getForEntity(environment);
            } else if (Collection.class.isAssignableFrom(returnValue.getClass())) {
                //设置分页和过滤条件
                return super.getResult(environment);
            } else {
                //TODO 只能抛错
                throw new RuntimeException("返回类型不对头，mutation中不允许出现该类型");
            }
        }
    }


    private void validate(MutationMethodMetaInfo mutationMethodMetaInfo, Object[] realArguments) {
        try {
            Validation.buildDefaultValidatorFactory().getValidator().forExecutables()
                    .validateParameters(mutationMethodMetaInfo.getTarget(), this.mutationMethodMetaInfo.getProxyMethod(), realArguments);
            ValidateAspect.validate(this.mutationMethodMetaInfo.getTarget(), this.mutationMethodMetaInfo.getProperMethod()
                    , realArguments, Validation.buildDefaultValidatorFactory().getValidator().forExecutables(),
                    new LocalVariableTableParameterNameDiscoverer());
        } catch (ValidException ve) {
            ve.printStackTrace();
            String msg = ve.getMessage();
            throw new ValidateErrorException(ve.getValidSelectErrors().stream().collect(Collectors.toMap(error -> error.getMessage(), error -> new String[]{error.getMessage()})));
        }
    }

    static final class ValidateErrorException extends RuntimeException implements GraphQLError

    {

        Map<String, Object> messageRelatePropsMap = null;

        public ValidateErrorException(Map<String, String[]> messageRelatePropsMap) {
            super("输入数据错误");
            this.messageRelatePropsMap = messageRelatePropsMap.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey()
                    , (e) -> StringUtils.collectionToCommaDelimitedString(Arrays.asList(e.getValue()))));

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
