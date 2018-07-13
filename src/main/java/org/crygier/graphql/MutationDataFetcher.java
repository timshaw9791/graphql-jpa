package org.crygier.graphql;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import graphql.execution.DataFetcherResult;
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

public class MutationDataFetcher extends JpaDataFetcher implements DataFetcher {

    //protected EntityManager entityManager;
    protected Method controllerMethod;
    List<GraphQLArgument> gqalist = null;
    Object target = null;

    public MutationDataFetcher(EntityManager entityManager, EntityType entityType,Method controllerMethod, Object target,List<GraphQLArgument> gqalist) {
        super(entityManager,entityType);

        this.controllerMethod = controllerMethod;
        this.gqalist = gqalist;
        this.target = target;
        //TODO 考虑标量，和实体类型，暂不考虑数组。
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

        //异常处理 TODO，输入检查错误,权限错误,业务逻辑错误，其他错误.
       /* try {
            //returnValue=
                    this.controllerMethod.invoke(target,realArguments);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/

        //说明返回的是简单类型
        if(this.entityType==null){
            return returnValue;
        }else {//if(returnValue==null){
    //        return null;
       // }else if(returnValue.getClass().isAssignableFrom(this.entityType.getJavaType())){//拿到主键
            String id= "1001";
          /*  try {
              //  id = (String)returnValue.getClass().getMethod("getId",String.class).invoke(returnValue);
                id="1001";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        */    environment.getField().getArguments().clear();
            environment.getField().getArguments().add(new Argument("id",new StringValue(id)));
            return super.get(environment);
       // }else{
            //TODO  报错
        //    return null;
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
