package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.IEntity;
import graphql.Scalars;
import graphql.language.*;
import graphql.schema.*;
import org.springframework.beans.BeanUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static graphql.Scalars.GraphQLString;

public class MutationDataFetcher extends CollectionJpaDataFetcher {

    //protected EntityManager entityManager;
    protected Method controllerMethod;
    List<GraphQLArgument> gqalist = null;
    Object target = null;

    public MutationDataFetcher(EntityManager entityManager, EntityType entityType, Method controllerMethod, Object target, List<GraphQLArgument> gqalist, IGraphQlTypeMapper graphQlTypeMapper) {
        super(entityManager, entityType,graphQlTypeMapper);
        this.controllerMethod = controllerMethod;
        this.gqalist = gqalist;
        this.target = target;
    }



    @Override
    public Object getResult(DataFetchingEnvironment environment) {
        Field field = environment.getFields().iterator().next();
        //TODO get mutation name,argument type.
        // field.getArguments().stream()

        Object[] realArguments = new Object[gqalist.size()];
        Object returnValue = null;

        field.getArguments().forEach(realArg ->
        {
            IntStream.range(0, gqalist.size())
                    .forEach(idx -> {
                        if (gqalist.get(idx).getName().equals(realArg.getName())) {
                            GraphQLType graphQLType = this.gqalist.get(idx).getType();
                            try {
                                realArguments[idx] = convertValue(environment,(GraphQLInputType)graphQLType, realArg.getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("getArguments erroros!");
                            }
                        }
                    });
        });

        //异常处理 TODO，输入检查错误,权限错误,业务逻辑错误，其他错误.
        try {
            returnValue =
                    this.controllerMethod.invoke(target, realArguments);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

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


}
