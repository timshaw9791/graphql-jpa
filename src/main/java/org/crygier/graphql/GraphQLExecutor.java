package org.crygier.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A GraphQL executor capable of constructing a {@link GraphQLSchema} from a JPA {@link EntityManager}. The executor
 * uses the constructed schema to execute queries directly from the JPA data source.
 * <p>
 * If the executor is given a mutator function, it is feasible to manipulate the {@link GraphQLSchema}, introducing
 * the option to add mutations, subscriptions etc.
 */

@Component
public class GraphQLExecutor implements ApplicationListener {

    @Resource
    private EntityManager entityManager;



    private GraphQL graphQL;
    private GraphQLSchema graphQLSchema;
    private GraphQLSchema.Builder builder;

    Map<Class,GraphQLScalarType> customGraphQLScalarTypeMap=null;

    public GraphQLExecutor() {
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given {@link EntityManager}.
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     *                      {@link javax.persistence.Entity} is extracted as {@link GraphQLSchema} objects.
     */
    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public GraphQLExecutor(EntityManager entityManager, Map<Class,GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.entityManager = entityManager;
        this.customGraphQLScalarTypeMap=customGraphQLScalarTypeMap;

    }


    //TODO 应该使用springApplication的生命周期时间来拿到这些bean
    private synchronized void createGraphQL(ApplicationContext listableBeanFactory) {

        Map<Method, Object> methodTargetMap = new HashMap<>();
        if(listableBeanFactory!=null) {
            Collection<Object> controllerObjects = listableBeanFactory.getBeansWithAnnotation(RestController.class)
                    .values().stream().filter(obj -> AnnotationUtils.findAnnotation(obj.getClass(),GRestController.class)!=null
                    ).collect(Collectors.toList());

            controllerObjects.stream()
                    .forEach(controllerObj -> {
                        Arrays.stream(controllerObj.getClass().getDeclaredMethods())
                                .forEach(method -> {
                                    if(AnnotationUtils.findAnnotation(method,GRequestMapping.class)!=null) {
                                        methodTargetMap.put(method, controllerObj);
                                    }
                                });
                    });
        }



        if (entityManager != null) {
            if (builder == null && this.customGraphQLScalarTypeMap == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager,methodTargetMap);
            } else if (builder == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, methodTargetMap, customGraphQLScalarTypeMap);
            }
            this.graphQLSchema = builder.build();
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).instrumentation(new MutationReturnInstrumentation()).build();
        }
    }

    /**
     * @return The {@link GraphQLSchema} used by this executor.
     */
    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }

    @Transactional
    public ExecutionResult execute(String query) {
        return graphQL.execute(query);
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        ExecutionResult executionResult= innerExecute(query,arguments);
        return executionResult;
    }



    public ExecutionResult innerExecute(String query, Map<String, Object> arguments) {
        if (arguments == null)
            return graphQL.execute(query);
        return graphQL.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build());
    }
//------------以下似乎都是用来写测试的，可以去掉这些东西------------
    /**
     * Gets the builder that was used to create the Schema that this executor is basing its query executions on. The
     * builder can be used to update the executor with the {@link #updateSchema(GraphQLSchema.Builder)} method.
     * @return An instance of a builder.
     */
    public GraphQLSchema.Builder getBuilder() {
        return builder;
    }

    /**
     * Returns the schema that this executor bases its queries on.
     * @return An instance of a {@link GraphQLSchema}.
     */
    public GraphQLSchema getSchema() {
        return graphQLSchema;
    }

    /**
     * Uses the given builder to re-create and replace the {@link GraphQLSchema}
     * that this executor uses to execute its queries.
     *
     * @param builder The builder to recreate the current {@link GraphQLSchema} and {@link GraphQL} instances.
     * @return The same executor but with a new {@link GraphQL} schema.
     */
    public GraphQLExecutor updateSchema(GraphQLSchema.Builder builder) {
        this.builder = builder;
        createGraphQL(null);
        return this;
    }


    public GraphQLExecutor updateSchema(GraphQLSchema.Builder builder, Map<Class,GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.builder = builder;
        this.customGraphQLScalarTypeMap=customGraphQLScalarTypeMap;
        createGraphQL(null);
        return this;
    }
    //------------以上似乎都是用来写测试的，可以去掉这些东西------------
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationStartedEvent){
            this.createGraphQL( ((ApplicationStartedEvent) event).getApplicationContext());
        }
    }
}
