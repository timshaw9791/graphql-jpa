package org.crygier.graphql;

import graphql.Scalars;
import graphql.schema.*;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.GraphQLIgnore;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.crygier.graphql.ExtendedJpaDataFetcher.*;

/**
 * A wrapper for the {@link GraphQLSchema.Builder}. In addition to exposing the traditional builder functionality,
 * this class constructs an initial {@link GraphQLSchema} by scanning the given {@link EntityManager} for relevant
 * JPA entities. This happens at construction time.
 * <p>
 * Note: This class should not be accessed outside this library.
 */
public class GraphQLSchemaBuilder extends GraphQLSchema.Builder implements IGraphQlTypeMapper {
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginationRequest";
    public static final String QFILTER_REQUEST_PARAM_NAME = "qfilter";
    public static final String MUTATION_INPUTTYPE_POSTFIX = "_";

    private final EntityManager entityManager;
    private final Map<Method, Object> methodTargetMap = new HashMap<>();

    private final Map<Class, GraphQLScalarType> classGraphQlScalarTypeMap = new HashMap<>();
    private final Map<Class, GraphQLEnumType> enumClassGraphQlEnumTypeMap = new HashMap<>();
    //所有的JPA Entity，Embeddable 对应的GraphQLType，包含GraphQLOutputObjectType和GraphQLInputObjectType两个类型，而且也也只有这两个类型
    private final Map<GraphQLType, ManagedType> graphQlTypeManagedTypeClassMap = new HashMap<>();


    /**
     * Initialises the builder with the given {@link EntityManager} from which we immediately start to scan for
     * entities to include in the GraphQL schema.
     *
     * @param entityManager The manager containing the data models to include in the final GraphQL schema.
     */
    public GraphQLSchemaBuilder(EntityManager entityManager, Map<Method, Object> methodTargetMap) {
        this(entityManager, methodTargetMap, null);
    }

    public GraphQLSchemaBuilder(EntityManager entityManager, Map<Method, Object> methodTargetMap, Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.entityManager = entityManager;
        //attributeMappers.stream().forEach(attributeMapper -> {this.customClassGraphQlScalarTypeMap.put(attributeMapper.getClass(), JavaScalars.GraphQLUUID);});
        this.classGraphQlScalarTypeMap.put(String.class, Scalars.GraphQLString);
        this.classGraphQlScalarTypeMap.put(Integer.class, Scalars.GraphQLInt);
        this.classGraphQlScalarTypeMap.put(int.class, Scalars.GraphQLInt);
        this.classGraphQlScalarTypeMap.put(float.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(Float.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(long.class, Scalars.GraphQLLong);
        this.classGraphQlScalarTypeMap.put(Long.class, Scalars.GraphQLLong);
        this.classGraphQlScalarTypeMap.put(boolean.class, Scalars.GraphQLBoolean);
        this.classGraphQlScalarTypeMap.put(Boolean.class, Scalars.GraphQLBoolean);
        this.classGraphQlScalarTypeMap.put(BigDecimal.class, Scalars.GraphQLBigDecimal);
        this.classGraphQlScalarTypeMap.put(short.class, Scalars.GraphQLShort);
        this.classGraphQlScalarTypeMap.put(Short.class, Scalars.GraphQLShort);
        this.classGraphQlScalarTypeMap.put(UUID.class, JavaScalars.GraphQLUUID);
        this.classGraphQlScalarTypeMap.put(Date.class, JavaScalars.GraphQLDate);
        this.classGraphQlScalarTypeMap.put(LocalDateTime.class, JavaScalars.GraphQLLocalDateTime);
        this.classGraphQlScalarTypeMap.put(Instant.class, JavaScalars.GraphQLInstant);
        this.classGraphQlScalarTypeMap.put(LocalDate.class, JavaScalars.GraphQLLocalDate);
        Optional.ofNullable(customGraphQLScalarTypeMap).ifPresent(map -> this.classGraphQlScalarTypeMap.putAll(map));

        this.methodTargetMap.putAll(methodTargetMap);

        this.prepareInputTypeStreamForMutation();
        super.query(getQueryType()).mutation(getMutationType());
    }

    /**
     * 把在mutation中可能会用到的输入类型放进去，这与在查询中用到的参数输入类型有所不同，在名称上他们相差一个后缀，在结构上query参数类型只包含标量，而mutation参数类型则包含嵌套对象（除了parent属性之外）
     * 根据实体模型获取所有可能的mutationInputType。
     *
     * @return
     */
    private void prepareInputTypeStreamForMutation() {
        Stream.concat(this.entityManager.getMetamodel().getEmbeddables().stream(), this.entityManager.getMetamodel().getEntities().stream())
                .filter(this::isNotIgnored)//内嵌类型embeded类型的
                .filter(distinctByKey(o -> o.getJavaType()))//根据java类型去重
                .forEach(type -> {
                    GraphQLInputType inputObjectType = newInputObject()
                            .name(type.getJavaType().getSimpleName() + MUTATION_INPUTTYPE_POSTFIX)
                            .description(getSchemaDocumentation(type.getJavaType()))
                            .fields(type.getAttributes().stream()
                                    .filter(attribute -> !"parent".equals(attribute.getName()))//去掉parent属性
                                    .filter(this::isNotIgnored)//去掉忽略属性
                                    .map(this::getInputObjectField)//根据字段属性获取对应字段。GraphQLInputTypeObjectFiled
                                    .collect(Collectors.toList()))
                            .build();
                    this.graphQlTypeManagedTypeClassMap.put(inputObjectType, type);
                    this.additionalType(inputObjectType);//添加到特别类型中，以便最终能做typereference的解析。
                });
    }

    /**
     * 根据k来过滤的断言， TODO 可以挪到stream的工具中去
     *
     * @param keyExtractor 拿到key的function
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * @return A freshly built {@link GraphQLSchema}
     * @deprecated Use {@link #build()} instead.
     */
    @Deprecated()
    public GraphQLSchema getGraphQLSchema() {
        return super.build();
    }

    GraphQLObjectType getQueryType() {
        GraphQLObjectType.Builder queryType = newObject().name("QueryType_JPA").description("DDD领域模型下的JPA查询");
        //TODO 要将所有的分录Entry排除,类型必须有，但不是顶级的，无法从此处开始查询数据，必须从顶级实体开始查询。
        queryType.fields(Stream.concat(entityManager.getMetamodel().getEntities().stream(), entityManager.getMetamodel().getEmbeddables().stream())
                .filter(this::isNotIgnored).map(this::getQueryFieldDefinition).collect(Collectors.toList()));

        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));
        return queryType.build();
    }


    private GraphQLObjectType getMutationType() {

        GraphQLObjectType.Builder queryType = newObject().name("Mutation_SpringMVC").description("将所有的SpringMVC.Controller中的Requestmapping方法暴露出来了");
        queryType.fields(this.methodTargetMap.entrySet().stream().map(entry -> {
            String grc = entry.getValue().getClass().getAnnotation(GRestController.class).value();
            String grm = entry.getKey().getAnnotation(GRequestMapping.class).path()[0];
            String mutationFieldName = ("/" + grc + grm).replace("//", "/").replace("/", "_").substring(1);
            List<GraphQLArgument> gqalist = getMutationGraphQLArgumentsByMethod(entry.getKey());
            EntityType entityType = entityManager.getMetamodel().getEntities().stream()
                    .filter(et -> et.getJavaType().equals(entry.getKey().getReturnType())).findFirst().orElse(null);
            return newFieldDefinition()
                    .name(mutationFieldName)
                    .description(getSchemaDocumentation((AnnotatedElement) entry.getKey()))
                    .type(getGraphQLTypeFromClassType(entry.getKey().getReturnType()))
                    .dataFetcher(new MutationDataFetcher(entityManager, entityType, entry.getKey(), entry.getValue(), gqalist, this))
                    .argument(gqalist)
                    .build();
        }).collect(Collectors.toList()));
        return queryType.build();
    }


    private List<GraphQLArgument> getMutationGraphQLArgumentsByMethod(Method targetMethod) {
        List<GraphQLArgument> gqalist = new ArrayList<>();

        Arrays.stream(targetMethod.getParameters()).forEach(parameter ->
        {
            //TODO 如果不能取出来，报错。因为所有的参数都需要暴露给graphql mutation
            RequestParam rp = (RequestParam) Arrays.stream(parameter.getAnnotations())
                    .filter(annotation -> annotation.annotationType().equals(RequestParam.class)).findFirst().get();

            String typeName = rp.name();//TODO rp.value()
            boolean required = rp.required();
            String defaultValue = rp.defaultValue();//TODO

            boolean isCollection = false;
            Class typeClazz = parameter.getType();
            if (parameter.isVarArgs()) {
                isCollection = true;
                typeClazz = parameter.getType();
            } else if (typeClazz.isAssignableFrom(Collection.class)) {
                isCollection = true;
                typeClazz = parameter.getParameterizedType().getClass();//TODO 如果泛型没写好，那会抛异常吗？
            }
            GraphQLInputType graphQLObjectInputType = getGraphQLInputTypeFromClassType(typeClazz);
            graphQLObjectInputType = isCollection ? new GraphQLList(graphQLObjectInputType) : graphQLObjectInputType;
            graphQLObjectInputType = required ? GraphQLNonNull.nonNull(graphQLObjectInputType) : graphQLObjectInputType;
            GraphQLArgument graphQLObjectField = GraphQLArgument.newArgument()
                    .name(typeName)
                    .type(graphQLObjectInputType)
                    .build();
            gqalist.add(graphQLObjectField);
        });

        return gqalist;
    }


    GraphQLFieldDefinition getQueryFieldDefinition(ManagedType<?> managedType) {
        return Optional.of(newFieldDefinition()
                .name(managedType.getJavaType().getSimpleName())
                .description(getSchemaDocumentation(managedType.getJavaType())))
                .map(fieldDefinition -> {
                    if (managedType instanceof EntityType) {
                        fieldDefinition.type(getGraphQLOutputType(managedType)).dataFetcher(new JpaDataFetcher(entityManager, (EntityType) managedType));
                    } else {
                        fieldDefinition.type(new GraphQLList(getGraphQLOutputType(managedType)));
                    }
                    return fieldDefinition;
                }).get()
                .argument(managedType.getAttributes().stream().filter(this::isValidInput).filter(this::isNotIgnored).flatMap(this::getArgument).collect(Collectors.toList()))
                .build();
    }


    //查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = newObject()
                .name(entityType.getName() + "Connection")
                .description("'Connection' response wrapper object for " + entityType.getName() + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("The actual object results").type(new GraphQLList(getGraphQLOutputType(entityType))).build())
                .build();

        return newFieldDefinition()
                .name(entityType.getName() + "Connection")
                .description("'Connection' request wrapper object for " + entityType.getName() + ".  Use this object in a query to request things like pagination or aggregation in an argument.  Use the 'content' field to request actual fields ")
                .type(pageType)
                //采用的是ExtendedJpaDataFetcher来处理
                .dataFetcher(new ExtendedJpaDataFetcher(entityManager, entityType))
                .argument(paginationArgument)
                .argument(roleArgument)
                .build();
    }

    @Override
    public Class getClazzByInputType(GraphQLType graphQLType) {
        return Optional.ofNullable(this.graphQlTypeManagedTypeClassMap.get(graphQLType)).map(entityType -> (Class) entityType.getJavaType())
                .orElseGet(() -> this.enumClassGraphQlEnumTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                        .map(entry -> entry.getKey()).findFirst()
                        .orElseGet(() -> this.classGraphQlScalarTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                                .map(entry -> entry.getKey()).findFirst()
                                .orElse(null)));

    }

    /**
     * 依次从四个map中寻找符合条件的记录对应的GraphQLInputType，如果找到则返回，如果没有找到，最终抛出异常。类似于四个if语句
     *
     * @param typeClazz
     * @return
     */
    public GraphQLInputType getGraphQLInputTypeFromClassType(Class typeClazz) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(typeClazz)).orElseGet(() ->
                Optional.ofNullable((GraphQLInputType) this.enumClassGraphQlEnumTypeMap.get(typeClazz)).orElseGet(() ->

                        (GraphQLInputType) this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                .filter(entry -> entry.getValue().getJavaType().equals(typeClazz) && entry.getKey() instanceof GraphQLInputType)
                                .map(entry -> (GraphQLInputType) entry.getKey()).findFirst().orElseThrow(() -> new RuntimeException("error getGraphQLInputTypeFromClassType!" + typeClazz.getCanonicalName()))));
    }

    private GraphQLOutputType getGraphQLTypeFromClassType(Class typeClazz) {
        return Optional.ofNullable((GraphQLOutputType) this.classGraphQlScalarTypeMap.get(typeClazz)).orElseGet(() ->
                Optional.ofNullable((GraphQLOutputType) this.enumClassGraphQlEnumTypeMap.get(typeClazz)).orElseGet(() ->
                        (GraphQLOutputType) this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                .filter(entry -> entry.getValue().getJavaType().equals(typeClazz) && entry.getKey() instanceof GraphQLOutputType)
                                .map(entry -> (GraphQLOutputType) entry.getKey()).findFirst().orElseThrow(() -> new RuntimeException("error getGraphQLTypeFromClassType!"))));
    }

    private Stream<GraphQLArgument> getArgument(Attribute attribute) {
        return Stream.of(getAttributeType(attribute))
                .filter(type -> type instanceof GraphQLInputType)
                .filter(type -> attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.EMBEDDED ||
                        (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED && type instanceof GraphQLScalarType))
                .map(type -> {
                    String name = attribute.getName();
                    return GraphQLArgument.newArgument()
                            .name(name)
                            .type((GraphQLInputType) type)
                            .build();
                });
    }


    GraphQLOutputType getGraphQLOutputType(ManagedType<?> managedType) {
        return this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(managedType) && entry.getKey() instanceof GraphQLOutputType)
                .map(entry -> (GraphQLOutputType) entry.getKey()).findFirst().orElseGet(() -> {
                    GraphQLObjectType answer = newObject()
                            .name(managedType.getJavaType().getSimpleName())
                            .description(getSchemaDocumentation(managedType.getJavaType()))
                            .description(getSchemaDocumentation(managedType.getJavaType()))
                            .fields(managedType.getAttributes().stream().filter(this::isNotIgnored).flatMap(this::getObjectField).collect(Collectors.toList()))
                            .build();
                    this.graphQlTypeManagedTypeClassMap.put(answer, managedType);
                    return answer;
                });
    }

    private GraphQLInputObjectField getInputObjectField(Attribute attribute) {
        return newInputObjectField()
                .name(attribute.getName())
                .description(getSchemaDocumentation(attribute.getJavaMember()))
                .type(getAttributeInputType(attribute))
                .build();
    }


    private Stream<GraphQLFieldDefinition> getObjectField(Attribute attribute) {
        return Stream.of(getAttributeType(attribute))
                .filter(type -> type instanceof GraphQLOutputType)
                .map(type -> {
                    List<GraphQLArgument> arguments = new ArrayList<>();
                    arguments.add(GraphQLArgument.newArgument().name("orderBy").type(orderByDirectionEnum).build());            // Always add the orderBy argument

                    // Get the fields that can be queried on (i.e. Simple Types, no Sub-Objects)
                    if (attribute instanceof SingularAttribute
                            && attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                        ManagedType foreignType = (ManagedType) ((SingularAttribute) attribute).getType();

                        Stream<Attribute> attributes = findBasicAttributes(foreignType.getAttributes());
                        attributes.forEach(it -> {
                            arguments.add(GraphQLArgument.newArgument()
                                    .name(it.getName())
                                    .type((GraphQLInputType) getAttributeType(it))
                                    .build());
                        });
                    }

                    String name = attribute.getName();

                    return newFieldDefinition()
                            .name(name)
                            .description(getSchemaDocumentation(attribute.getJavaMember()))
                            .type((GraphQLOutputType) type)
                            .argument(arguments)
                            .build();
                });
    }

    private Stream<Attribute> findBasicAttributes(Collection<Attribute> attributes) {
        return attributes.stream().filter(this::isNotIgnored).filter(it -> it.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC);
    }


    private GraphQLInputType getBasicAttributeInputTypeAndAddEnumIfNecessary(Class javaType) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(javaType))
                .orElseGet(() -> Optional.ofNullable((GraphQLInputType) this.enumClassGraphQlEnumTypeMap.get(javaType)).orElseGet(() -> {
                    if (javaType.isEnum()) {
                        GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(javaType.getSimpleName());
                        int ordinal = 0;
                        for (Enum enumValue : ((Class<Enum>) javaType).getEnumConstants()) {
                            enumBuilder.value(enumValue.name(), ordinal++);
                        }
                        GraphQLEnumType answer = enumBuilder.build();
                        setIdentityCoercing(answer);
                        this.enumClassGraphQlEnumTypeMap.put(javaType, answer);
                        return answer;
                    } else {
                        return null;
                    }
                }));
    }

    /**
     * 找到基础类型的类型，包括枚举enum
     *
     * @param javaType
     * @return 如果没找到，则回返回null
     */
    private GraphQLType getBasicAttributeTypeAndAddEnumIfNecessary(Class javaType) {
        return Optional.ofNullable((GraphQLType) this.classGraphQlScalarTypeMap.get(javaType))
                .orElseGet(() -> Optional.ofNullable((GraphQLType) this.enumClassGraphQlEnumTypeMap.get(javaType)).orElseGet(() -> {
                    if (javaType.isEnum()) {
                        GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(javaType.getSimpleName());
                        int ordinal = 0;
                        for (Enum enumValue : ((Class<Enum>) javaType).getEnumConstants()) {
                            enumBuilder.value(enumValue.name(), ordinal++);
                        }
                        GraphQLEnumType answer = enumBuilder.build();
                        setIdentityCoercing(answer);
                        this.enumClassGraphQlEnumTypeMap.put(javaType, answer);
                        return answer;
                    } else {
                        return null;
                    }
                }));
    }

    private GraphQLInputType getAttributeInputType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLInputType graphQLInputType = getBasicAttributeInputTypeAndAddEnumIfNecessary(attribute.getJavaType());
            if (graphQLInputType != null) {
                return graphQLInputType;
            }
        }
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName() + MUTATION_INPUTTYPE_POSTFIX));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName() + MUTATION_INPUTTYPE_POSTFIX);
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            //TODO 应该不用了，因为我们的集合体现在manytoone中
            Type foreignType = ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(getBasicAttributeInputTypeAndAddEnumIfNecessary(foreignType.getJavaType()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
            EmbeddableType<?> embeddableType = (EmbeddableType<?>) ((SingularAttribute<?, ?>) attribute).getType();
            return new GraphQLTypeReference(embeddableType.getJavaType().getSimpleName() + MUTATION_INPUTTYPE_POSTFIX);
        }

        final String declaringType = attribute.getDeclaringType().getJavaType().getName(); // fully qualified name of the entity class
        final String declaringMember = attribute.getJavaMember().getName(); // field name in the entity class

        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + declaringMember + "' of entity class '" + declaringType + "'");

    }

    private GraphQLType getAttributeType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLType graphQLType = getBasicAttributeTypeAndAddEnumIfNecessary(attribute.getJavaType());
            if (graphQLType != null) {
                return graphQLType;
            }
        }
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            //这里的引用只能引用到GraphQLType而不可能是GraphInputQL类型
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName());
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            Type foreignType = ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(getBasicAttributeTypeAndAddEnumIfNecessary(foreignType.getJavaType()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
            EmbeddableType<?> embeddableType = (EmbeddableType<?>) ((SingularAttribute<?, ?>) attribute).getType();
            return new GraphQLTypeReference(embeddableType.getJavaType().getSimpleName());
        }

        final String declaringType = attribute.getDeclaringType().getJavaType().getName(); // fully qualified name of the entity class
        final String declaringMember = attribute.getJavaMember().getName(); // field name in the entity class

        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + declaringMember + "' of entity class '" + declaringType + "'");
    }

    private boolean isValidInput(Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC ||
                attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION ||
                attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED;
    }

    private String getSchemaDocumentation(Member member) {
        if (member instanceof AnnotatedElement) {
            return getSchemaDocumentation((AnnotatedElement) member);
        }

        return null;
    }

    private String getSchemaDocumentation(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            SchemaDocumentation schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation.class);
            return schemaDocumentation != null ? schemaDocumentation.value() : null;
        }

        return null;
    }

    private boolean isNotIgnored(Attribute attribute) {
        return isNotIgnored(attribute.getJavaMember()) && isNotIgnored(attribute.getJavaType());
    }

    private boolean isNotIgnored(ManagedType entityType) {
        return isNotIgnored(entityType.getJavaType());
    }

    private boolean isNotIgnored(Member member) {
        return member instanceof AnnotatedElement && isNotIgnored((AnnotatedElement) member);
    }

    private boolean isNotIgnored(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            GraphQLIgnore schemaDocumentation = annotatedElement.getAnnotation(GraphQLIgnore.class);
            return schemaDocumentation == null;
        }
        return false;
    }

    /**
     * A bit of a hack, since JPA will deserialize our Enum's for us...we don't want GraphQL doing it.
     *
     * @param type
     */
    private void setIdentityCoercing(GraphQLType type) {
        try {
            Field coercing = type.getClass().getDeclaredField("coercing");
            coercing.setAccessible(true);
            coercing.set(type, new IdentityCoercing());
        } catch (Exception e) {
            log.error("Unable to set coercing for " + type, e);
        }
    }

    private static final GraphQLArgument paginationArgument =
            GraphQLArgument.newArgument()
                    .name(PAGINATION_REQUEST_PARAM_NAME)
                    .type(newInputObject()
                            .name("PaginationObject")
                            .description("Query object for Pagination Requests, specifying the requested page, and that page's size.\n\nNOTE: 'page' parameter is 1-indexed, NOT 0-indexed.\n\nExample: paginationRequest { page: 1, size: 20 }")
                            .field(newInputObjectField().name("page").description("Which page should be returned, starting with 1 (1-indexed)").type(Scalars.GraphQLInt).build())
                            .field(newInputObjectField().name("size").description("How many results should this page contain").type(Scalars.GraphQLInt).build())
                            .build()
                    ).build();


    GraphQLArgument roleArgument =
            GraphQLArgument.newArgument()
                    .name(QFILTER_REQUEST_PARAM_NAME)
                    .type(newInputObject()
                            .name("Qfilter")
                            .description("过滤表达式")
                            .field(newInputObjectField().name(QFILTER_KEY).description("键：role.id或者role.privilegeItem.name").type(GraphQLString).build())
                            .field(newInputObjectField().name(QFILTER_VALUE).description("值：现在所有的都用字符串，或者null,或者适用于like的 '%abc'").type(GraphQLString).build())
                            .field(newInputObjectField().name(QFILTER_OPERATE).description("操作符:>,<，=，notnull，isnul，等，后续需要改为枚举").type(queryFilterOperatorEnum).build())
                            .field(newInputObjectField().name(QFILTER_ANDOR).description("后续改为枚举，AND，ON").type(queryFilterCombinatorEnum).build())
                            .field(newInputObjectField().name(QFILTER_NEXT).description("下一个，或者为null").type(GraphQLTypeReference.typeRef("Qfilter")))
                            .build()).build();


    private static final GraphQLEnumType orderByDirectionEnum =
            GraphQLEnumType.newEnum()
                    .name("OrderByDirection")
                    .description("Describes the direction (Ascending / Descending) to sort a field.")
                    .value("ASC", 0, "Ascending")
                    .value("DESC", 1, "Descending")
                    .build();

    private static final GraphQLEnumType queryFilterOperatorEnum = new GraphQLEnumType("QueryFilterOperator",
            "查询过滤操作符", Arrays.stream(QueryFilterOperator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(), qfo.getDescription(), qfo.getValue())).collect(Collectors.toList()));

    private static final GraphQLEnumType queryFilterCombinatorEnum = new GraphQLEnumType("QueryFilterCombinator",
            "查询表达式组合操作符", Arrays.stream(QueryFilterCombinator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(), qfo.getDescription(), qfo.getValue())).collect(Collectors.toList()));


}
