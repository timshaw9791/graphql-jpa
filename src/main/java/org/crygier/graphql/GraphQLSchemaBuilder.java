package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import cn.wzvtcsoft.x.bos.domain.ICoreObject;
import graphql.Scalars;
import graphql.schema.*;
import org.crygier.graphql.annotation.GraphQLIgnore;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Type;
import java.lang.reflect.*;
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

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * A wrapper for the {@link GraphQLSchema.Builder}. In addition to exposing the traditional builder functionality,
 * this class constructs an initial {@link GraphQLSchema} by scanning the given {@link EntityManager} for relevant
 * JPA entities. This happens at construction time.
 * <p>
 * Note: This class should not be accessed outside this library.
 */
public class GraphQLSchemaBuilder extends GraphQLSchema.Builder implements IGraphQlTypeMapper {
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginator";
    public static final String QFILTER_REQUEST_PARAM_NAME = "qfilter";
    public static final String MUTATION_INPUTTYPE_POSTFIX = "_";
    public static final String LISTQUERY_FILTER_POSTFIX = "__";
    public static final String ENTRY_PARENT_PROPNAME = "parent";
    public static final String ENTITY_LIST_NAME = "List";
    public static final String[] QUERY_OUTPUT_FILTER_PROPS = {"id", "number"};

    private final EntityManager entityManager;
    private final Map<Method, Object> methodTargetMap = new HashMap<>();

    private final Map<Class, GraphQLScalarType> classGraphQlScalarTypeMap = new HashMap<>();
    private final Map<Class<? extends BosEnum>, GraphQLEnumType> enumClassGraphQlEnumTypeMap = new HashMap<>();
    //所有的JPA Entity，Embeddable 对应的GraphQLType，包含GraphQLOutputObjectType和GraphQLInputObjectType两个类型，而且也也只有这两个类型
    private final Map<GraphQLType, ManagedType> graphQlTypeManagedTypeClassMap = new HashMap<>();
    //要用到的常见用来输入的非实体类。
    private final Map<Class, GraphQLInputType> dtoClassGraphQlInputTypeMap = new HashMap<>();
    //mutation下的输入entity类型中，需要忽略以下字段
    public static final Set<String> ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT = new HashSet<String>(Arrays.asList(new String[]{"parent", "createtime", "updatetime", "createactorid", "updateactorid"}));

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
        this.enumClassGraphQlEnumTypeMap.put(QueryFilterCombinator.class, queryFilterCombinatorEnum);
        this.enumClassGraphQlEnumTypeMap.put(QueryFilterOperator.class, queryFilterOperatorEnum);
        this.enumClassGraphQlEnumTypeMap.put(OrderByDirection.class, orderByDirectionEnum);
        this.enumClassGraphQlEnumTypeMap.put(FieldNullEnum.class, fieldNullEnum);


        this.dtoClassGraphQlInputTypeMap.put(QueryFilter.class, getDtoInputType(QFILTER_REQUEST_PARAM_NAME, QueryFilter.class));
        this.dtoClassGraphQlInputTypeMap.put(Paginator.class, getDtoInputType(PAGINATION_REQUEST_PARAM_NAME, Paginator.class));

        this.prepareInputTypeStreamForMutation();
        super.query(getQueryType()).mutation(getMutationType());
    }

    private GraphQLInputObjectType getDtoInputType(String graphQLInputObjectTypeName, Class clazz) {
        return new GraphQLInputObjectType(graphQLInputObjectTypeName, AnnotationUtils.findAnnotation(clazz, SchemaDocumentation.class).value()
                , Arrays.stream(clazz.getMethods()).filter(method -> AnnotationUtils.findAnnotation(method, SchemaDocumentation.class) != null)
                .map(method -> {
                    Class propType = BeanUtils.findPropertyForMethod(method).getPropertyType();
                    String propName = BeanUtils.findPropertyForMethod(method).getName();
                    String propDoc = AnnotationUtils.findAnnotation(method, SchemaDocumentation.class).value();
                    return newInputObjectField().name(propName).description(propDoc).type(
                            propType == clazz ? GraphQLTypeReference.typeRef(graphQLInputObjectTypeName) : this.getGraphQLInputTypeFromClassType(propType)

                    ).build();
                }).collect(Collectors.toList()));
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
                                    .filter(attribute -> !ENTRY_PARENT_PROPNAME.equals(attribute.getName()))//去掉parent属性
                                    .filter(this::isNotIgnoredForEntityInput)//去掉忽略属性
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
        GraphQLObjectType.Builder queryType = newObject().name("QueryType_JPA")
                .description("DDD领域模型下的JPA查询,所有类型均有createtime（创建时间属性),updatedtime(修改时间属性)");
        //TODO 要将所有的分录Entry排除,类型必须有，但不是顶级的，无法从此处开始查询数据，必须从顶级实体开始查询。
        queryType.fields(Stream.concat(entityManager.getMetamodel().getEntities().stream(), entityManager.getMetamodel().getEmbeddables().stream())
                .filter(this::isNotIgnored).map(this::getQueryFieldDefinition).collect(Collectors.toList()));

        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));
        return queryType.build();
    }


    private GraphQLObjectType getMutationType() {
        GraphQLObjectType.Builder queryType = newObject().name("Mutation_SpringMVC").description("将所有的SpringMVC.Controller中的Requestmapping方法暴露出来了");
        queryType.fields(this.methodTargetMap.entrySet().stream().map(entry -> {
            MutationMetaInfo mutationMetaInfo = new DefaultMutationMetaInfo(entry.getValue(), entry.getKey(), this, null);
            return Optional.of(newFieldDefinition()
                    .name(mutationMetaInfo.getMutationFieldName())
                    .description(getSchemaDocumentation((AnnotatedElement) entry.getKey())))

                    .map(fieldDefinition -> fieldDefinition
                            .type(mutationMetaInfo.getGraphQLOutputType())
                    ).get()
                    .dataFetcher(new MutationDataFetcher(entityManager, this, mutationMetaInfo))
                    .argument(mutationMetaInfo.getGraphQLArgumentList())
                    .build();
        }).filter(gfd -> gfd != null).collect(Collectors.toList()));
        return queryType.build();
    }


    GraphQLFieldDefinition getQueryFieldDefinition(ManagedType<?> managedType) {
        return Optional.of(newFieldDefinition()
                .name(managedType.getJavaType().getSimpleName())
                .description(getSchemaDocumentation(managedType.getJavaType())))
                .map(fieldDefinition -> {
                    if (managedType instanceof EntityType) {
                        fieldDefinition.type(getGraphQLOutputType(managedType)).dataFetcher(new JpaDataFetcher(entityManager, (EntityType) managedType, this));
                    } else {//这里是什么？ TODO 非实体类型的能被查询到吗？内嵌类型么？
                        fieldDefinition.type(new GraphQLList(getGraphQLOutputType(managedType)))
                                .dataFetcher(new CollectionJpaDataFetcher(entityManager, (EntityType) managedType, this));
                    }
                    return fieldDefinition;
                }).get()
                .argument(managedType.getAttributes().stream()
                        //只有id和number才能被当作单个实体对象的查询输入参数
                        .filter(attr -> new HashSet<String>(Arrays.asList(QUERY_OUTPUT_FILTER_PROPS)).contains(attr.getName()))
                        //.filter(this::isValidInput).filter(this::isNotIgnored)
                        .map(attr -> GraphQLArgument.newArgument()
                                .name(attr.getName())
                                .type(Scalars.GraphQLString)
                                .build()).collect(Collectors.toList()))
                .build();
    }


    //查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = newObject()
                .name(entityType.getName() + ENTITY_LIST_NAME)
                .description(entityType.getName() + ENTITY_LIST_NAME + " 负责包装一组" + entityType.getName() + "数据，你可以在查询中使用分页、排序、过滤等功能")
                .field(newFieldDefinition().name("totalPages").description("根据paginator.size和数据库记录数得出的总页数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("总的记录数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("实际返回的内容列表").type(new GraphQLList(getGraphQLOutputType(entityType))).build())
                .build();

       /* GraphQLInputObjectType filtertype = newInputObject()
                .name(entityType.getAlias() + LISTQUERY_FILTER_POSTFIX)
                .description(entityType.getAlias()+LISTQUERY_FILTER_POSTFIX+" 主要是用来负责在特定情况下制定过滤条件/排序规则的")
                .field(newInputFieldDefinition().name("totalPages").description("根据paginator.size和数据库记录数得出的总页数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("总的记录数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("实际返回的内容列表").type(new GraphQLList(getGraphQLOutputType(entityType))).build())
                .build();*/


        return newFieldDefinition()
                .name(entityType.getName() + ENTITY_LIST_NAME)
                .description(entityType.getName() + ENTITY_LIST_NAME + " 负责包装一组" + entityType.getName() + "数据，你可以在查询中使用分页、排序、过滤等功能,请使用content字段请求实际的字段 ")
                .type(pageType)
                //采用的是ExtendedJpaDataFetcher来处理
                .dataFetcher(new CollectionJpaDataFetcher(entityManager, entityType, this))
                .argument(GraphQLArgument.newArgument()
                        .name(PAGINATION_REQUEST_PARAM_NAME)
                        .type(this.dtoClassGraphQlInputTypeMap.get(Paginator.class))
                )
                .argument(GraphQLArgument.newArgument()
                        .name(QFILTER_REQUEST_PARAM_NAME)
                        .type(this.dtoClassGraphQlInputTypeMap.get(QueryFilter.class)))
                .build();
    }

    @Override
    public Class getClazzByInputType(GraphQLType graphQLType) {
        return Optional.ofNullable(this.graphQlTypeManagedTypeClassMap.get(graphQLType)).map(entityType -> (Class) entityType.getJavaType())
                .orElseGet(() -> this.enumClassGraphQlEnumTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                        .map(entry -> entry.getKey()).findFirst()
                        .orElseGet(() -> this.dtoClassGraphQlInputTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                                .map(entry -> entry.getKey()).findFirst()
                                .orElseGet(() -> this.classGraphQlScalarTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                                        .map(entry -> entry.getKey()).findFirst()
                                        .orElse(null))));
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
                        Optional.ofNullable((GraphQLInputType) this.dtoClassGraphQlInputTypeMap.get(typeClazz)).orElseGet(() ->
                                (GraphQLInputType) this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                        .filter(entry -> entry.getValue().getJavaType().equals(typeClazz) && entry.getKey() instanceof GraphQLInputType)
                                        .map(entry -> (GraphQLInputType) entry.getKey()).findFirst().orElseThrow(() -> new RuntimeException("error getGraphQLInputTypeFromClassType!" + typeClazz.getCanonicalName())))));
    }

    @Override
    public EntityType getEntityType(Class type) {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(et -> et.getJavaType().equals(type)).findFirst().orElse(null);
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


    @Override
    public GraphQLOutputType getGraphQLOutputType(ManagedType<?> managedType) {
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
                    Stream<GraphQLArgument> stream = getFilterQLArguments(attribute);
                    stream.forEach(arg -> arguments.add(arg));
                    String name = attribute.getName();
                    return newFieldDefinition()
                            .name(name)
                            .description(getSchemaDocumentation(attribute.getJavaMember()))
                            .type((GraphQLOutputType) type)
                            .argument(arguments)
                            .build();
                });
    }

    private Stream<GraphQLArgument> getFilterQLArguments(Attribute attribute) {
        List<GraphQLArgument> arguments = new ArrayList<>();

    /*    Class clazz = attribute.getJavaType();
        //if(String.class.isAssignableFrom(clazz)){
        arguments.add(GraphQLArgument.newArgument()
                .name("a")
                .type(fieldNullEnum)
                .build());
        arguments.add(GraphQLArgument.newArgument()
                .name("b")
                //
                .type(fieldNullEnum)
                .build());
        return arguments.stream();
*/
        //无操作数ISNUL,ISNOTNULL，1操作数EQ,LIKE，多操作数IN，计算序号order.
     /*   }
           // [eq,like],in, nil:[isnull,isnotnull]
            GraphQLList x=GraphQLList((GraphQLInputType) getAttributeType(attribute));
        }else if(Integer.class.isAssignableFrom(clazz)){
            [eq, gt lt,nlt,nlt],is:[null,notnull],in
        }else if(Boolean.class.isAssignableFrom(clazz)){
            eq[eq,noteq],is:[null,notnull]

    }else if(Float.class.isAssignableFrom(clazz)){
            [eq, gt lt,nlt,nlt],nil:[isnull,isnotnull]
        }else if(Float.class.isAssignableFrom(clazz)){
            [eq, gt lt,nlt,nlt],nil:[isnull,isnotnull]

        if (attribute instanceof SingularAttribute
                && attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
            ManagedType foreignType = (ManagedType) ((SingularAttribute) attribute).getType();
            Stream<Attribute> attributes = findBasicAttributes(foreignType.getAttributes());
            attributes.forEach(it -> {
                arguments.add(GraphQLArgument.newArgument()
                        .name(it.getAlias())
                        .type((GraphQLInputType) getAttributeType(it))
                        .build());
            });
        }

*/
        return arguments.stream();
    }


    private Stream<Attribute> findBasicAttributes(Collection<Attribute> attributes) {
        return attributes.stream().filter(this::isNotIgnored).filter(it -> it.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC);
    }

    /**
     * 找到基础类型的类型，包括枚举enum
     *
     * @param javaType
     * @return 如果没找到，则回返回null
     */
    private GraphQLType getBasicAttributeTypeAndAddEnumIfNecessary(Class javaType) {

        return Optional.ofNullable((GraphQLType) this.classGraphQlScalarTypeMap.get(javaType))
                .orElseGet(() -> Optional.ofNullable((GraphQLType) this.enumClassGraphQlEnumTypeMap.get(javaType))
                        .orElseGet(() -> {
                            if (javaType.isEnum() && (BosEnum.class.isAssignableFrom(javaType))) {
                                GraphQLEnumType gt = getGraphQLEnumType(javaType);
                                this.enumClassGraphQlEnumTypeMap.put((Class<BosEnum>) javaType, gt);
                                return gt;
                            } else {
                                return null;
                            }
                        }));
    }

    private GraphQLInputType getAttributeInputType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLInputType graphQLInputType = (GraphQLInputType) getBasicAttributeTypeAndAddEnumIfNecessary(attribute.getJavaType());
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
            return new GraphQLList(getBasicAttributeTypeAndAddEnumIfNecessary(foreignType.getJavaType()));
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

    private static String getSchemaDocumentation(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            SchemaDocumentation schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation.class);
            return schemaDocumentation != null ? schemaDocumentation.value() : null;
        }
        return null;
    }

    private boolean isNotIgnoredForEntityInput(Attribute attribute) {
        return !ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT.contains(attribute.getName()) && (isNotIgnored(attribute.getJavaMember()) && isNotIgnored(attribute.getJavaType()));
    }

    private boolean isNotIgnored(Attribute attribute) {
        boolean isEntryParent = ICoreObject.class.equals(attribute.getJavaType()) && ENTRY_PARENT_PROPNAME.equals(attribute.getName());
        return !isEntryParent && (isNotIgnored(attribute.getJavaMember()) && isNotIgnored(attribute.getJavaType()));
    }

    private boolean isNotIgnored(ManagedType entityType) {
        return ICoreObject.class.isAssignableFrom(entityType.getJavaType()) && isNotIgnored(entityType.getJavaType());
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


    private static final GraphQLEnumType fieldNullEnum = getGraphQLEnumType(FieldNullEnum.class);

    private static GraphQLEnumType getGraphQLEnumType(Class<? extends BosEnum> bosEnumClass) {
        return new GraphQLEnumType(bosEnumClass.getSimpleName(), getSchemaDocumentation(bosEnumClass), Arrays.stream(bosEnumClass.getEnumConstants())
                .map(qfo -> new GraphQLEnumValueDefinition(((BosEnum) qfo).getValue(), ((BosEnum) qfo).getDescription(), ((BosEnum) qfo).getValue()))
                .collect(Collectors.toList()));
    }


    private static final GraphQLEnumType orderByDirectionEnum = getGraphQLEnumType(OrderByDirection.class);

    private static final GraphQLEnumType queryFilterOperatorEnum = getGraphQLEnumType(QueryFilterOperator.class);

    private static final GraphQLEnumType queryFilterCombinatorEnum = getGraphQLEnumType(QueryFilterCombinator.class);


}


enum OrderByDirection implements BosEnum {

    ASC("ASC", "升序", "升序排列"), DESC("DESC", "降序", "降序排列");

    private OrderByDirection(String value, String name, String description) {
        this.ev = new BosEnum.EnumInnerValue(value, name, description);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

}

@SchemaDocumentation("分页器")
class Paginator {
    private int page;
    private int size;

    public Paginator() {

    }

    public Paginator(int page, int size) {
        this.page = page;
        this.size = size;
    }


    @SchemaDocumentation("当前页号（从1开始）")
    public void setPage(int page) {
        this.page = page;
    }

    @SchemaDocumentation("每页大小")
    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

}
