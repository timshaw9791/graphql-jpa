package org.crygier.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A GraphQL executor capable of constructing a {@link GraphQLSchema} from a JPA {@link EntityManager}. The executor
 * uses the constructed schema to execute queries directly from the JPA data source.
 * <p>
 * If the executor is given a mutator function, it is feasible to manipulate the {@link GraphQLSchema}, introducing
 * the option to add mutations, subscriptions etc.
 */
public class GraphQLExecutor {

    @Resource
    private EntityManager entityManager;


    private ListableBeanFactory listableBeanFactory;
    Map<String,Method> mutationFeildNameMethodMap=new HashMap<>();

    private GraphQL graphQL;
    private GraphQLSchema graphQLSchema;
    private GraphQLSchema.Builder builder;

    protected GraphQLExecutor(ListableBeanFactory listableBeanFactory) {
        this.listableBeanFactory=listableBeanFactory;
        createGraphQL(null,mutationFeildNameMethodMap);
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given {@link EntityManager}.
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     *                      {@link javax.persistence.Entity} is extracted as {@link GraphQLSchema} objects.
     */
    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        createGraphQL(null,mutationFeildNameMethodMap);
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given {@link EntityManager}.
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     *                      {@link javax.persistence.Entity} is extracted as {@link GraphQLSchema} objects.
     * @param attributeMappers Custom {@link AttributeMapper} list, if you need any non-standard mappings.
     */
    public GraphQLExecutor(EntityManager entityManager, Collection<AttributeMapper> attributeMappers) {
        this.entityManager = entityManager;
        createGraphQL(attributeMappers,mutationFeildNameMethodMap);
    }

    @PostConstruct
    protected synchronized void createGraphQL() {
        Map<String, Object> controllers;
        controllers = listableBeanFactory.getBeansWithAnnotation(RestController.class);

        controllers.entrySet().stream()
                .filter(entry->(entry.getValue().getClass().getAnnotation(GRestController.class)!=null))
                .forEach(entry->{
                    Arrays.stream(entry.getValue().getClass().getDeclaredMethods())
                            .filter(method->method.getAnnotation(GRequestMapping.class)!=null)
                            .forEach(method->{
                                String grc=entry.getValue().getClass().getAnnotation(GRestController.class).value();
                                String grm=method.getAnnotation(GRequestMapping.class).path()[0];
                                String mutationFieldName=("/"+grc+grm).replace("//","/").replace("/","_").substring(1);
                                mutationFeildNameMethodMap.put(mutationFieldName,method);
                            });
                });



        createGraphQL(null,mutationFeildNameMethodMap);
    }

    protected synchronized void createGraphQL(Collection<AttributeMapper> attributeMappers,Map<String,Method> mutationFeildNameMethodMap) {
        if (entityManager != null) {
            if (builder == null && attributeMappers == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager,mutationFeildNameMethodMap);
            } else if (builder == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, mutationFeildNameMethodMap,attributeMappers);
            }
            this.graphQLSchema = builder.build();
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
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
        if (arguments == null)
            return graphQL.execute(query);
        return graphQL.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build());
    }

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
        createGraphQL(null,mutationFeildNameMethodMap);
        return this;
    }

    /**
     * Uses the given builder to re-create and replace the {@link GraphQLSchema}
     * that this executor uses to execute its queries.
     *
     * @param builder The builder to recreate the current {@link GraphQLSchema} and {@link GraphQL} instances.
     * @param attributeMappers Custom {@link AttributeMapper} list, if you need any non-standard mappings.
     * @return The same executor but with a new {@link GraphQL} schema.
     */
    public GraphQLExecutor updateSchema(GraphQLSchema.Builder builder, Collection<AttributeMapper> attributeMappers) {
        this.builder = builder;
        createGraphQL(attributeMappers,mutationFeildNameMethodMap);
        return this;
    }

}
