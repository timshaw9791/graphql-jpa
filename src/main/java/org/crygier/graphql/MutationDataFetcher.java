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

public class MutationDataFetcher extends JpaDataFetcher implements DataFetcher {

    //protected EntityManager entityManager;
    protected Method controllerMethod;
    List<GraphQLArgument> gqalist = null;
    Object target = null;
    IGraphQlTypeMapper graphQlTypeMapper=null;

    public MutationDataFetcher(EntityManager entityManager, EntityType entityType,Method controllerMethod, Object target,List<GraphQLArgument> gqalist,IGraphQlTypeMapper graphQlTypeMapper) {
        super(entityManager,entityType);

        this.controllerMethod = controllerMethod;
        this.gqalist = gqalist;
        this.target = target;
        this.graphQlTypeMapper=graphQlTypeMapper;
        //TODO 考虑标量，和实体类型，暂不考虑数组。
    }

    private Object composeRealArgument(GraphQLType graphQLType, Value realArgValue) throws IllegalAccessException, InstantiationException {
        //如果为非空类型
        if (graphQLType instanceof GraphQLNonNull) {
            graphQLType = ((GraphQLNonNull) graphQLType).getWrappedType();
            if (realArgValue == null) {
                throw new RuntimeException("composeRealArgument error!not null");
            } else {
                return composeRealArgument(graphQLType, realArgValue);
            }
        } else if (realArgValue == null) {//否则如果为空
            return null;

        } else if (graphQLType instanceof GraphQLScalarType) {//如果为标量
            Object value=null;
             if(realArgValue instanceof StringValue){
                 value =((StringValue)realArgValue).getValue();
            }else if(realArgValue instanceof IntValue) {
                 value = ((IntValue) realArgValue).getValue();
             }
            return ((GraphQLScalarType) graphQLType).getCoercing().parseValue(value);
        } else if (graphQLType instanceof GraphQLList) {//如果为列表
            graphQLType = ((GraphQLList) graphQLType).getWrappedType();
            //TODO 需要修改
            return composeRealArgument(graphQLType, realArgValue);
        } else if (graphQLType instanceof GraphQLInputObjectType && (realArgValue instanceof ObjectValue)) {
            Class realclass = this.graphQlTypeMapper.getClazzByInputType(graphQLType);
            Object instance = realclass.newInstance();
            ((ObjectValue) realArgValue).getObjectFields().stream().forEach(objectField -> {
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(realclass, objectField.getName());
                GraphQLType graphQLType2 = this.graphQlTypeMapper.getGraphQLInputTypeFromClassType(propertyDescriptor.getPropertyType());
               try {
                   Object propertyValue = composeRealArgument(graphQLType2, objectField.getValue());
                   propertyDescriptor.getWriteMethod().invoke(instance, propertyValue);
               }catch(Exception e){
                   e.printStackTrace();
                   throw new RuntimeException("getArguments erroros!");
               }
            });
            return instance;
        } else {
            throw new RuntimeException("composeRealArgument error!");
        }
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
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
                            GraphQLType graphQLType=this.gqalist.get(idx).getType();
                            try{
                                realArguments[idx] = composeRealArgument(graphQLType, realArg.getValue());
                            }catch(Exception e){
                                e.printStackTrace();
                                throw new RuntimeException("getArguments erroros!");
                            }
                        }
                    });
        });

        //异常处理 TODO，输入检查错误,权限错误,业务逻辑错误，其他错误.
        try {
            returnValue=
                    this.controllerMethod.invoke(target,realArguments);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //说明返回的是简单类型
        if(this.entityType==null){
            return returnValue;
        }else if(returnValue==null){
            return null;
        }else if(returnValue.getClass().isAssignableFrom(IEntity.class)){//拿到主键
            String id=((IEntity)returnValue).getId();
            environment.getField().getArguments().clear();
            environment.getField().getArguments().add(new Argument("id",new StringValue(id)));
            return super.get(environment);
        }else{
            //TODO  报错
            return null;
       }
    }

    private Attribute getAttribute(DataFetchingEnvironment environment, Argument argument) {
        GraphQLObjectType objectType = getObjectType(environment, argument);
        EntityType entityType = getEntityType(objectType);
        return entityType.getAttribute(argument.getName());
    }

    private EntityType getEntityType(GraphQLObjectType objectType) {
        return entityManager.getMetamodel().getEntities().stream().filter(it -> it.getName().equals(objectType.getName())).findFirst().get();
    }



    private GraphQLObjectType getObjectType(DataFetchingEnvironment environment, Argument argument) {
        GraphQLType graphQLType = environment.getFieldDefinition().getArgument(argument.getName()).getType();

        if (graphQLType instanceof GraphQLNonNull) {
            graphQLType = ((GraphQLNonNull) graphQLType).getWrappedType();
        }

        if (graphQLType instanceof GraphQLObjectType) {
            return (GraphQLObjectType) graphQLType;
        }
        return null;
    }
}
