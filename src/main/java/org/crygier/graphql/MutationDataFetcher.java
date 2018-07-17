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
    IGraphQlTypeMapper graphQlTypeMapper = null;

    public MutationDataFetcher(EntityManager entityManager, EntityType entityType, Method controllerMethod, Object target, List<GraphQLArgument> gqalist, IGraphQlTypeMapper graphQlTypeMapper) {
        super(entityManager, entityType);
        this.controllerMethod = controllerMethod;
        this.gqalist = gqalist;
        this.target = target;
        this.graphQlTypeMapper = graphQlTypeMapper;
        //TODO 考虑标量，和实体类型。
    }

    /**
     * 还有枚举类型和，变量引用类型没有处理 TODO
     *
     * @param graphQLType
     * @param realArgValue
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private Object composeRealArgument(GraphQLType graphQLType, Value realArgValue) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //如果为非空类型
        if (graphQLType instanceof GraphQLNonNull) {
            graphQLType = ((GraphQLNonNull) graphQLType).getWrappedType();
            return composeRealArgument(graphQLType, realArgValue);
        } else if (realArgValue == null) {//否则如果为空
            return null;
        } else if (graphQLType instanceof GraphQLScalarType) {//如果为标量 //TODO 需要把这部分放到GraphQLSchemaBuilder中，因为具体有哪些标量类型他那里最清楚。
            Object value = null;
            if (realArgValue instanceof StringValue) {
                value = ((StringValue) realArgValue).getValue();
            } else if (realArgValue instanceof IntValue) {
                value = ((IntValue) realArgValue).getValue();
            }

            return ((GraphQLScalarType) graphQLType).getCoercing().parseValue(value);
        } else if (graphQLType instanceof GraphQLList) {//如果为列表
            final GraphQLType wrapptype = ((GraphQLList) graphQLType).getWrappedType();
            Set resultSet = new HashSet();
            ArrayValue av = null;
            if (realArgValue instanceof ArrayValue) {
                av = (ArrayValue) realArgValue;
            } else {
                List<Value> vlist = new ArrayList<Value>();
                vlist.add(realArgValue);
                av = new ArrayValue(vlist);
            }
            List<Value> listvalue = av.getValues();
            for (Value value : listvalue) {
                resultSet.add(composeRealArgument(((GraphQLInputObjectType) wrapptype), value));
            }
            return resultSet;
        } else if (graphQLType instanceof GraphQLInputObjectType) {
            Class realclass = this.graphQlTypeMapper.getClazzByInputType(graphQLType);
            Object instance = realclass.newInstance();
            List<ObjectField> fieldlist = ((ObjectValue) realArgValue).getObjectFields();
            for (ObjectField objectField : fieldlist) {
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(realclass, objectField.getName());
                GraphQLInputType subtype = ((GraphQLInputObjectType) graphQLType).getFieldDefinition(objectField.getName()).getType();
                Object propertyValue = composeRealArgument(subtype, objectField.getValue());
                propertyDescriptor.getWriteMethod().invoke(instance, propertyValue);
            }
            return instance;
        } else {
            throw new RuntimeException("MutationDataFetcher.composeRealArgument error!");
        }
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
                                realArguments[idx] = composeRealArgument(graphQLType, realArg.getValue());
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
