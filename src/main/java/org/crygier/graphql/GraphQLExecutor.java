package org.crygier.graphql;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.TypeFromAST;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.schema.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A GraphQL executor capable of constructing a {@link GraphQLSchema} from a JPA {@link EntityManager}. The executor
 * uses the constructed schema to execute queries directly from the JPA data source.
 * <p>
 * If the executor is given a mutator function, it is feasible to manipulate the {@link GraphQLSchema}, introducing
 * the option to add mutations, subscriptions etc.
 */

@Component
public class GraphQLExecutor implements ApplicationListener, IGraphQLExecutor {

    @Resource
    private EntityManager entityManager;


    private GraphQL graphQL;
    private GraphQLSchema graphQLSchema;
    private GraphQLSchema.Builder builder;

    Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap = null;

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

    public GraphQLExecutor(EntityManager entityManager, Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.entityManager = entityManager;
        this.customGraphQLScalarTypeMap = customGraphQLScalarTypeMap;

    }

    //TODO 应该使用springApplication的生命周期时间来拿到这些bean
    private synchronized void createGraphQL(ApplicationContext listableBeanFactory) {

        Map<Method, Object> methodTargetMap = new HashMap<>();
        if (listableBeanFactory != null) {
            Collection<Object> controllerObjects = listableBeanFactory.getBeansWithAnnotation(RestController.class)
                    .values().stream().filter(obj -> AnnotationUtils.findAnnotation(obj.getClass(), GRestController.class) != null
                    ).collect(Collectors.toList());

            controllerObjects.stream()
                    .forEach(controllerObj -> {
                        Arrays.stream(controllerObj.getClass().getDeclaredMethods())
                                .forEach(method -> {
                                    if (AnnotationUtils.findAnnotation(method, GRequestMapping.class) != null) {
                                        methodTargetMap.put(method, controllerObj);
                                    }
                                });
                    });
        }

        if (entityManager != null) {
            if (builder == null && this.customGraphQLScalarTypeMap == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, methodTargetMap);
            } else if (builder == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, methodTargetMap, customGraphQLScalarTypeMap);
            }
            this.graphQLSchema = builder.build();
            this.cache = Caffeine.newBuilder().maximumSize(10_000).build();
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).preparsedDocumentProvider(cache::get).instrumentation(new MutationReturnInstrumentation()).build();
        }
    }

    private Cache<String, PreparsedDocumentEntry> cache = null;

    /**
     * @return The {@link GraphQLSchema} used by this executor.
     */
    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }

    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        ExecutionResult executionResult = innerExecute(query, arguments);
        return executionResult;
    }

    @Override
    public IGraphQlTypeMapper getGraphQlTypeMapper() {
        return (GraphQLSchemaBuilder) this.builder;
    }

    public ExecutionResult innerExecute(String query, Map<String, Object> arguments) {
        if (arguments == null)
            return graphQL.execute(query);
        return graphQL.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            try{
                this.createGraphQL(((ApplicationStartedEvent) event).getApplicationContext());
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("初始化graphql字段和类型不成功！");
            }

        }
    }

    @Override
    public GraphQLType getGraphQLType(Type type) {
        return TypeFromAST.getTypeFromAST(this.graphQLSchema, type);
    }

    @Override
    public OperationDefinition getOperationDefinition(String query) {
        PreparsedDocumentEntry entry = this.cache.getIfPresent(query);
        Document doc = null;
        if (entry != null) {
            doc = entry.getDocument();
        } else {
            try {
                doc = new Parser().parseDocument(query);
            } catch (ParseCancellationException e) {
                e.printStackTrace();
                throw new RuntimeException("GraphQLExecutor.getOperationDefinition...");
            }
        }
        return NodeUtil.getOperation(doc, null).operationDefinition;
    }

}
