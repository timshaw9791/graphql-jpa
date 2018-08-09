package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import cn.wzvtcsoft.x.bos.domain.ICoreObject;
import cn.wzvtcsoft.x.bos.domain.util.BosUtils;
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
import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final String MUTATION_INPUTTYPE_POSTFIX = "_";
    private static final String ENTRY_PARENT_PROPNAME = "parent";
    private static final String ENTITY_LIST_NAME = "List";
    private static final String[] QUERY_OUTPUT_FILTER_PROPS = {"id", "number"};
    //mutation下的输入entity类型中，需要忽略以下字段
    private static final Set<String> ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT = new HashSet<String>(Arrays.asList(new String[]{"parent", "createtime", "updatetime", "createactorid", "updateactorid"}));

    private final EntityManager entityManager;
    private final Map<Method, Object> methodTargetMap = new HashMap<>();

    private final Map<Class, GraphQLScalarType> classGraphQlScalarTypeMap = new HashMap<>();
    private final Map<Class<? extends BosEnum>, GraphQLEnumType> enumClassGraphQlEnumTypeMap = new HashMap<>();
    //所有的JPA Entity，Embeddable 对应的GraphQLType，包含GraphQLOutputObjectType和GraphQLInputObjectType两个类型，而且也也只有这两个类型
    private final Map<GraphQLType, ManagedType> graphQlTypeManagedTypeClassMap = new HashMap<>();
    //要用到的常见用来输入的非实体类。
    private final Map<Class, GraphQLInputType> dtoClassGraphQlInputTypeMap = new HashMap<>();

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

        this.prepareArgumentInputTypeForMutations();
        super.query(getQueryType()).mutation(getMutationType());
    }

    /**
     * 简单的DTO输入类型的类型
     *
     * @param graphQLInputObjectTypeName
     * @param clazz
     * @return
     */
    private GraphQLInputObjectType getDtoInputType(String graphQLInputObjectTypeName, Class clazz) {
        return new GraphQLInputObjectType(graphQLInputObjectTypeName, AnnotationUtils.findAnnotation(clazz, SchemaDocumentation.class).value()
                , Arrays.stream(clazz.getMethods()).filter(method -> AnnotationUtils.findAnnotation(method, SchemaDocumentation.class) != null)
                .map(method -> {
                    Class propType = BeanUtils.findPropertyForMethod(method).getPropertyType();
                    String propName = BeanUtils.findPropertyForMethod(method).getName();
                    String propDoc = AnnotationUtils.findAnnotation(method, SchemaDocumentation.class).value();
                    return newInputObjectField().name(propName).description(propDoc).type(
                            propType == clazz ? GraphQLTypeReference.typeRef(graphQLInputObjectTypeName) : this.getGraphQLInputType(propType)
                    ).build();
                }).collect(Collectors.toList()));
    }

    /**
     * 把在mutation中可能会用到的输入类型放进去，这与在查询中用到的参数输入类型有所不同，在名称上他们相差一个后缀，在结构上query参数类型只包含标量，而mutation参数类型则包含嵌套对象（除了parent属性之外）
     * 根据实体模型获取所有可能的mutationInputType。
     *
     * @return
     */
    private void prepareArgumentInputTypeForMutations() {
        this.entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).filter(BosUtils.distinctByKey(o -> o.getJavaType()))//根据java类型去重
                .forEach(type -> {
                    GraphQLInputType inputObjectType = newInputObject()
                            .name(type.getJavaType().getSimpleName() + MUTATION_INPUTTYPE_POSTFIX)
                            .description(getSchemaDocumentation(type.getJavaType()))
                            .fields(type.getAttributes().stream()
                                    .filter(this::isNotIgnoredForEntityInput)//去掉忽略属性
                                    .map(attribute -> newInputObjectField()
                                            .name(attribute.getName())
                                            .description(getSchemaDocumentation(attribute.getJavaMember()))
                                            .type((GraphQLInputType) getAttributeGrahQLType(attribute, true))
                                            .build())
                                    //根据字段属性获取对应字段。GraphQLInputTypeObjectFiled
                                    .collect(Collectors.toList()))
                            .build();
                    this.graphQlTypeManagedTypeClassMap.put(inputObjectType, type);
                    this.additionalType(inputObjectType);//添加到特别类型中，以便最终能做typereference的解析。
                });
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
        queryType.fields(entityManager.getMetamodel().getEntities().stream()
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

    GraphQLFieldDefinition getQueryFieldDefinition(EntityType<?> entityType) {
        return Optional.of(newFieldDefinition()
                .name(entityType.getJavaType().getSimpleName())
                .description(getSchemaDocumentation(entityType.getJavaType())))
                .map(fieldDefinition -> {
                    fieldDefinition.type(
                            getGraphQLOutputTypeAndCreateIfNecessary(entityType))
                            .dataFetcher(new JpaDataFetcher(entityManager, (EntityType) entityType, this));
                    return fieldDefinition;
                }).get()
                .argument(entityType.getAttributes().stream()
                        //只有id和number才能被当作单个实体对象的查询输入参数
                        .filter(attr -> new HashSet<String>(Arrays.asList(QUERY_OUTPUT_FILTER_PROPS)).contains(attr.getName()))
                        .map(attr -> GraphQLArgument.newArgument()
                                .name(attr.getName())
                                .type(Scalars.GraphQLString)
                                .build()).collect(Collectors.toList()))
                .build();
    }

    //查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = newObject()
                .name(getGraphQLTypeNameOfEntityList(entityType))
                .description(getGraphQLTypeNameOfEntityList(entityType) + " 负责包装一组" + entityType.getName() + "数据，你可以在查询中使用分页、排序、过滤等功能")
                .field(newFieldDefinition().name("totalPages").description("根据paginator.size和数据库记录数得出的总页数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("总的记录数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("实际返回的内容列表").type(new GraphQLList(this.getGraphQLOutputTypeAndCreateIfNecessary(entityType))).build())
                .build();

        return newFieldDefinition()
                .name(getGraphQLTypeNameOfEntityList(entityType))
                .description(getGraphQLTypeNameOfEntityList(entityType) + " 负责包装一组" + entityType.getName() + "数据，你可以在查询中使用分页、排序、过滤等功能,请使用content字段请求实际的字段 ")
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


/*

    private GraphQLInputType getGraphQLInputTypeAndCreateIfNecessary(EntityType entityType){
        return Optional.ofNullable(getGraphQLInputType(entityType.getJavaType())).orElseGet(()->{
            GraphQLInputObjectType graphQLInputObjectType = newObject().name(entityType.getJavaType().getSimpleName())
                    .fields((List<GraphQLFieldDefinition>) entityType.getAttributes().stream()
                            .filter(attr->this.isNotIgnored((Attribute)attr))
                            .map(attribute ->
                                    newFieldDefinition()
                                            .description(getSchemaDocumentation(((Attribute) attribute).getJavaMember()))
                                            .name(((Attribute) attribute).getName())
                                            .type((GraphQLType) this.getAttributeGrahQLType((Attribute) attribute, true)
                                            ).build()

                            ).collect(Collectors.toList())).build();
            this.graphQlTypeManagedTypeClassMap.put(graphQLInputObjectType, entityType);
            return graphQLInputObjectType;
        });
    }
    */

    /**
     * 依次从map队列中寻找符合条件的记录对应的GraphQLInputType，如果找到则返回，如果没有找到，最终抛出异常。类似于四个if语句
     *
     * @param typeClazz
     * @return
     */
    @Override
    public GraphQLInputType getGraphQLInputType(Type typeClazz) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(typeClazz)).orElseGet(() ->
                Optional.ofNullable((GraphQLInputType) this.enumClassGraphQlEnumTypeMap.get(typeClazz)).orElseGet(() ->
                        Optional.ofNullable((GraphQLInputType) this.dtoClassGraphQlInputTypeMap.get(typeClazz)).orElseGet(() ->
                                (GraphQLInputType) this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                        .filter(entry -> entry.getValue().getJavaType().equals(typeClazz) && entry.getKey() instanceof GraphQLInputType)
                                        .map(entry -> (GraphQLInputType) entry.getKey()).findFirst()
                                        .orElseThrow(null))));
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

    @Override
    public String getGraphQLTypeNameOfEntityList(EntityType entityType) {
        return entityType.getName() + ENTITY_LIST_NAME;
    }

    @Override
    public BosEnum getBosEnumByValue(GraphQLEnumType bosEnumType, String enumValue) {
        Class<? extends BosEnum> enumType = this.getClazzByInputType(bosEnumType);
        return  Arrays.stream(enumType.getEnumConstants()).filter(bosEnum -> bosEnum.getValue().equals(enumValue)).findFirst()
                        .orElse(null);
    }

    @Override
    public EntityType getEntityType(Class type) {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(et -> et.getJavaType().equals(type)).findFirst().orElse(null);
    }

    private GraphQLOutputType getGraphQLOutputTypeAndCreateIfNecessary(EntityType entityType) {
        return Optional.ofNullable(getGraphQLOutputType(entityType.getJavaType())).orElseGet(() -> {
            GraphQLObjectType graphQLObjectType = newObject().name(entityType.getJavaType().getSimpleName())
                    .fields((List<GraphQLFieldDefinition>) entityType.getAttributes().stream()
                            .filter(attr -> this.isNotIgnored((Attribute) attr))
                            .map(attribute -> {
                                GraphQLOutputType outputType=(GraphQLOutputType) this.getAttributeGrahQLType((Attribute) attribute, false);
                                GraphQLFieldDefinition.Builder builder=newFieldDefinition()
                                        .description(getSchemaDocumentation(((Attribute) attribute).getJavaMember()))
                                        .name(((Attribute) attribute).getName())
                                        .type(outputType);
                                 if(outputType instanceof  GraphQLScalarType){
                                     builder.argument(GraphQLArgument.newArgument()
                                             .name(OrderByDirection.ORDER_BY)
                                             .type(orderByDirectionEnum)
                                     );
                                 }
                                 return builder.build();
                            }).collect(Collectors.toList())).build();
            this.graphQlTypeManagedTypeClassMap.put(graphQLObjectType, entityType);
            return graphQLObjectType;
        });
    }

    @Override
    public GraphQLOutputType getGraphQLOutputType(Type type) {
        return Optional.ofNullable((GraphQLOutputType) this.classGraphQlScalarTypeMap.get(type)).orElseGet(() ->
                Optional.ofNullable((GraphQLOutputType) this.enumClassGraphQlEnumTypeMap.get(type)).orElseGet(() ->
                        (GraphQLOutputType) this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                .filter(entry -> entry.getValue().getJavaType().equals(type) && entry.getKey() instanceof GraphQLOutputType)
                                .map(entry -> (GraphQLOutputType) entry.getKey()).findFirst().orElse(null)));
    }

    /**
     * 根据atrribute来查找对应的GraphQLType，如果是InputType属性，则结果可以被强制转换为GraphQLInputType
     *
     * @param attribute
     * @param needInputType -需要返回InputType，则返回的必定是GraphQLInputType
     * @return
     */
    private GraphQLType getAttributeGrahQLType(Attribute attribute, boolean needInputType) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLInputType graphQLInputType = (GraphQLInputType) getBasicAttributeTypeAndAddBosEnumIfNecessary(attribute.getJavaType());
            if (graphQLInputType != null) {
                return graphQLInputType;
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName() + (needInputType?MUTATION_INPUTTYPE_POSTFIX:"")));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName() + (needInputType ? MUTATION_INPUTTYPE_POSTFIX : ""));
        }
        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + attribute.getJavaMember().getName() + "' of entity class '" + attribute.getDeclaringType().getJavaType().getName() + "'");
    }

    /**
     * 找到基础类型的类型，包括枚举enum ,如果没找到，则回返回null
     *
     * @param javaType
     * @return
     */
    private GraphQLInputType getBasicAttributeTypeAndAddBosEnumIfNecessary(Class javaType) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(javaType))
                .orElseGet(() -> Optional.ofNullable(this.enumClassGraphQlEnumTypeMap.get(javaType))
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

    private boolean isValidInput(Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC ||
                attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION ||
                attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED;
    }

    private String getSchemaDocumentation(Member member) {
        return (member instanceof AnnotatedElement) ? getSchemaDocumentation((AnnotatedElement) member) : null;
    }

    private static String getSchemaDocumentation(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            SchemaDocumentation schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation.class);
            return schemaDocumentation != null ? schemaDocumentation.value() : null;
        }
        return null;
    }

    private boolean isNotIgnoredForEntityInput(Attribute attribute) {
        return !ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT.contains(attribute.getName()) && isNotIgnored(attribute);
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


    private static final GraphQLEnumType fieldNullEnum = getGraphQLEnumType(FieldNullEnum.class);
    private static final GraphQLEnumType orderByDirectionEnum = getGraphQLEnumType(OrderByDirection.class);
    private static final GraphQLEnumType queryFilterOperatorEnum = getGraphQLEnumType(QueryFilterOperator.class);
    private static final GraphQLEnumType queryFilterCombinatorEnum = getGraphQLEnumType(QueryFilterCombinator.class);
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

    private static GraphQLEnumType getGraphQLEnumType(Class<? extends BosEnum> bosEnumClass) {
        return new GraphQLEnumType(bosEnumClass.getSimpleName(), getSchemaDocumentation(bosEnumClass), Arrays.stream(bosEnumClass.getEnumConstants())
                .map(qfo -> new GraphQLEnumValueDefinition(((BosEnum) qfo).getValue(), ((BosEnum) qfo).getDescription(), ((BosEnum) qfo).getValue()))
                .collect(Collectors.toList()));
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
