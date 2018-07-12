package org.crygier.graphql;

import graphql.language.*;
import graphql.schema.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MutationDataFetcher implements DataFetcher {

    protected EntityManager entityManager;
    protected Method controllerMethod;
    List<GraphQLArgument> gqalist = null;
    Object target = null;



    public MutationDataFetcher(EntityManager entityManager, Method controllerMethod, Object target,List<GraphQLArgument> gqalist) {
        this.entityManager = entityManager;
        this.controllerMethod = controllerMethod;
        this.gqalist = gqalist;
        this.target = target;
        //TODO 设置返回类型
        //考虑标量，和实体类型，暂不考虑数组。
        this.controllerMethod.getReturnType();
    }

    private Object composeRealArgument(GraphQLArgument graphQlArgument, Value realArgValue) {
        return null;
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
                            realArguments[idx] = composeRealArgument(this.gqalist.get(idx), realArg.getValue());

                        }
                    });
        });


        try {
            returnValue = this.controllerMethod.invoke(this.target, realArguments);

            //准备返回
            //如果是单个标量，直接返回
            // 如果是entity，要交给JPADataFetcher进行处理
        } catch (Exception e) {
            throw new RuntimeException("controllerMethod.invoke error!");
        }
        return returnValue;
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
