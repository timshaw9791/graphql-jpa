package org.crygier.graphql;

import graphql.Scalars;
import graphql.schema.*;
import org.crygier.graphql.annotation.GraphQLIgnore;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.crygier.graphql.ExtendedJpaDataFetcher.*;

/**
 * A wrapper for the {@link GraphQLSchema.Builder}. In addition to exposing the traditional builder functionality,
 * this class constructs an initial {@link GraphQLSchema} by scanning the given {@link EntityManager} for relevant
 * JPA entities. This happens at construction time.
 *
 * Note: This class should not be accessed outside this library.
 */
public class GraphQLSchemaBuilder extends GraphQLSchema.Builder {

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginationRequest";
    public static final String QFILTER_REQUEST_PARAM_NAME = "qfilter";
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    private final EntityManager entityManager;
    private final Map<Class, GraphQLType> classCache = new HashMap<>();
    private final Map<EmbeddableType<?>, GraphQLObjectType> embeddableCache = new HashMap<>();
    private final Map<EntityType, GraphQLObjectType> entityCache = new HashMap<>();
    private final List<AttributeMapper> attributeMappers = new ArrayList<>();

    /**
     * Initialises the builder with the given {@link EntityManager} from which we immediately start to scan for
     * entities to include in the GraphQL schema.
     * @param entityManager The manager containing the data models to include in the final GraphQL schema.
     */
    public GraphQLSchemaBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;

        populateStandardAttributeMappers();

        super.query(getQueryType());
    }

    public GraphQLSchemaBuilder(EntityManager entityManager, Collection<AttributeMapper> attributeMappers) {
        this.entityManager = entityManager;

        this.attributeMappers.addAll(attributeMappers);
        populateStandardAttributeMappers();

        super.query(getQueryType());
    }

    private void populateStandardAttributeMappers() {
        attributeMappers.add(createStandardAttributeMapper(UUID.class, JavaScalars.GraphQLUUID));
        attributeMappers.add(createStandardAttributeMapper(Date.class, JavaScalars.GraphQLDate));
        attributeMappers.add(createStandardAttributeMapper(LocalDateTime.class, JavaScalars.GraphQLLocalDateTime));
        attributeMappers.add(createStandardAttributeMapper(Instant.class, JavaScalars.GraphQLInstant));
        attributeMappers.add(createStandardAttributeMapper(LocalDate.class, JavaScalars.GraphQLLocalDate));
    }

    private AttributeMapper createStandardAttributeMapper(final Class<?> assignableClass, final GraphQLType type) {
        return (javaType) -> {
            if (assignableClass.isAssignableFrom(javaType))
                return Optional.of(type);

            return Optional.empty();
        };
    }

    /**
     * @deprecated Use {@link #build()} instead.
     * @return A freshly built {@link GraphQLSchema}
     */
    @Deprecated()
    public GraphQLSchema getGraphQLSchema() {
        return super.build();
    }

    GraphQLObjectType getQueryType() {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("QueryType_JPA").description("All encompassing schema for this JPA environment");
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldDefinition).collect(Collectors.toList()));
        //TODO 要将所有的分录Entry排除,类型必须有，但不是顶级的，无法从此处开始查询数据，必须从顶级实体开始查询。
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));
        //TODO 这个embedd类型的也可以先排除
        queryType.fields(entityManager.getMetamodel().getEmbeddables().stream().filter(this::isNotIgnored).map(this::getQueryEmbeddedFieldDefinition).collect(Collectors.toList()));

        return queryType.build();
    }

    GraphQLFieldDefinition getQueryFieldDefinition(EntityType<?> entityType) {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName())
                .description(getSchemaDocumentation(entityType.getJavaType()))
                .type(new GraphQLList(getObjectType(entityType)))
                .dataFetcher(new JpaDataFetcher(entityManager, entityType))
                .argument(entityType.getAttributes().stream().filter(this::isValidInput).filter(this::isNotIgnored).flatMap(this::getArgument).collect(Collectors.toList()))
                .build();
    }
    
    GraphQLFieldDefinition getQueryEmbeddedFieldDefinition(EmbeddableType<?> embeddableType) {
    	String embeddedName = embeddableType.getJavaType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(embeddedName)
                .description(getSchemaDocumentation(embeddableType.getJavaType()))
                .type(new GraphQLList(getObjectType(embeddableType)))
                .argument(embeddableType.getAttributes().stream().filter(this::isValidInput).filter(this::isNotIgnored).flatMap(this::getArgument).collect(Collectors.toList()))
                .build();
    }
//查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = GraphQLObjectType.newObject()
                .name(entityType.getName() + "Connection")
                .description("'Connection' response wrapper object for " + entityType.getName() + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("content").description("The actual object results").type(new GraphQLList(getObjectType(entityType))).build())
                .build();

        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName() + "Connection")
                .description("'Connection' request wrapper object for " + entityType.getName() + ".  Use this object in a query to request things like pagination or aggregation in an argument.  Use the 'content' field to request actual fields ")
                .type(pageType)
                //采用的是ExtendedJpaDataFetcher来处理
                .dataFetcher(new ExtendedJpaDataFetcher(entityManager, entityType))
                .argument(paginationArgument)
                .argument(roleArgument)
                .build();
    }

    private Stream<GraphQLArgument> getArgument(Attribute attribute) {
        return getAttributeType(attribute)
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

    GraphQLObjectType getObjectType(EntityType<?> entityType) {
        if (entityCache.containsKey(entityType))
            return entityCache.get(entityType);

        GraphQLObjectType answer = GraphQLObjectType.newObject()
                .name(entityType.getName())
                .description(getSchemaDocumentation(entityType.getJavaType()))
                .fields(entityType.getAttributes().stream().filter(this::isNotIgnored).flatMap(this::getObjectField).collect(Collectors.toList()))
                .build();

        entityCache.put(entityType, answer);

        return answer;
    }
    
    GraphQLObjectType getObjectType(EmbeddableType<?> embeddableType) {
    	
        if (embeddableCache.containsKey(embeddableType))
            return embeddableCache.get(embeddableType);

        String embeddableName= embeddableType.getJavaType().getSimpleName();
        GraphQLObjectType answer = GraphQLObjectType.newObject()
                .name(embeddableName)
                .description(getSchemaDocumentation(embeddableType.getJavaType()))
                .fields(embeddableType.getAttributes().stream().filter(this::isNotIgnored).flatMap(this::getObjectField).collect(Collectors.toList()))
                .build();

        embeddableCache.put(embeddableType, answer);

        return answer;
    }

    private Stream<GraphQLFieldDefinition> getObjectField(Attribute attribute) {
        return getAttributeType(attribute)
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
                                    .type((GraphQLInputType) getAttributeType(it).findFirst().get())
                                    .build());
                        });
                    }

                    String name = attribute.getName();
                    

                    return GraphQLFieldDefinition.newFieldDefinition()
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

    private GraphQLType getBasicAttributeType(Class javaType) {
        // First check our 'standard' and 'customized' Attribute Mappers.  Use them if possible
        Optional<AttributeMapper> customMapper = attributeMappers.stream()
                .filter(it -> it.getBasicAttributeType(javaType).isPresent())
                .findFirst();

        if (customMapper.isPresent())
            return customMapper.get().getBasicAttributeType(javaType).get();
        else if (String.class.isAssignableFrom(javaType))
            return Scalars.GraphQLString;
        else if (Integer.class.isAssignableFrom(javaType) || int.class.isAssignableFrom(javaType))
            return Scalars.GraphQLInt;
        else if (Short.class.isAssignableFrom(javaType) || short.class.isAssignableFrom(javaType))
            return Scalars.GraphQLShort;
        else if (Float.class.isAssignableFrom(javaType) || float.class.isAssignableFrom(javaType)
                || Double.class.isAssignableFrom(javaType) || double.class.isAssignableFrom(javaType))
            return Scalars.GraphQLFloat;
        else if (Long.class.isAssignableFrom(javaType) || long.class.isAssignableFrom(javaType))
            return Scalars.GraphQLLong;
        else if (Boolean.class.isAssignableFrom(javaType) || boolean.class.isAssignableFrom(javaType))
            return Scalars.GraphQLBoolean;
        else if (javaType.isEnum()) {
            return getTypeFromJavaType(javaType);
        } else if (BigDecimal.class.isAssignableFrom(javaType)) {
            return Scalars.GraphQLBigDecimal;
        }

        throw new UnsupportedOperationException(
                "Class could not be mapped to GraphQL: '" + javaType.getClass().getTypeName() + "'");
    }

    private Stream<GraphQLType> getAttributeType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            try {
                return Stream.of(getBasicAttributeType(attribute.getJavaType()));
            } catch (UnsupportedOperationException e) {
                //fall through to the exception below
                //which is more useful because it also contains the declaring member
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return Stream.of(new GraphQLList(new GraphQLTypeReference(foreignType.getName())));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return Stream.of(new GraphQLTypeReference(foreignType.getName()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            Type foreignType = ((PluralAttribute) attribute).getElementType();
            return Stream.of(new GraphQLList(getTypeFromJavaType(foreignType.getJavaType())));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
            EmbeddableType<?> embeddableType = (EmbeddableType<?>) ((SingularAttribute<?,?>) attribute).getType();
            return Stream.of(new GraphQLTypeReference(embeddableType.getJavaType().getSimpleName()));
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
    
    private boolean isNotIgnored(EmbeddableType<?> embeddableType) {
        return isNotIgnored(embeddableType.getJavaType());
    }

    private boolean isNotIgnored(EntityType entityType) {
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

    private GraphQLType getTypeFromJavaType(Class clazz) {
        if (clazz.isEnum()) {
            if (classCache.containsKey(clazz))
                return classCache.get(clazz);

            GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(clazz.getSimpleName());
            int ordinal = 0;
            for (Enum enumValue : ((Class<Enum>) clazz).getEnumConstants())
                enumBuilder.value(enumValue.name(), ordinal++);

            GraphQLType answer = enumBuilder.build();
            setIdentityCoercing(answer);

            classCache.put(clazz, answer);

            return answer;
        }

        return getBasicAttributeType(clazz);
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
                    .type(GraphQLInputObjectType.newInputObject()
                            .name("PaginationObject")
                            .description("Query object for Pagination Requests, specifying the requested page, and that page's size.\n\nNOTE: 'page' parameter is 1-indexed, NOT 0-indexed.\n\nExample: paginationRequest { page: 1, size: 20 }")
                            .field(GraphQLInputObjectField.newInputObjectField().name("page").description("Which page should be returned, starting with 1 (1-indexed)").type(Scalars.GraphQLInt).build())
                            .field(GraphQLInputObjectField.newInputObjectField().name("size").description("How many results should this page contain").type(Scalars.GraphQLInt).build())
                            .build()
                    ).build();


    GraphQLArgument roleArgument =
            GraphQLArgument.newArgument()
                    .name(QFILTER_REQUEST_PARAM_NAME)
                    .type(GraphQLInputObjectType.newInputObject()
                            .name("Qfilter")
                            .description("过滤表达式")
                            .field(GraphQLInputObjectField.newInputObjectField().name(QFILTER_KEY).description("键：role.id或者role.privilegeItem.name").type(Scalars.GraphQLString).build())
                            .field(GraphQLInputObjectField.newInputObjectField().name(QFILTER_VALUE).description("值：现在所有的都用字符串，或者null,或者适用于like的 '%abc'").type(Scalars.GraphQLString).build())
                            .field(GraphQLInputObjectField.newInputObjectField().name(QFILTER_OPERATE).description("操作符:>,<，=，notnull，isnul，等，后续需要改为枚举").type(queryFilterOperatorEnum).build())
                            .field(GraphQLInputObjectField.newInputObjectField().name(QFILTER_ANDOR).description("后续改为枚举，AND，ON").type(queryFilterCombinatorEnum).build())
                            .field(GraphQLInputObjectField.newInputObjectField().name(QFILTER_NEXT).description("下一个，或者为null").type(GraphQLTypeReference.typeRef("Qfilter")))
                            .build()).build();


    private static final GraphQLEnumType orderByDirectionEnum =
            GraphQLEnumType.newEnum()
                    .name("OrderByDirection")
                    .description("Describes the direction (Ascending / Descending) to sort a field.")
                    .value("ASC", 0, "Ascending")
                    .value("DESC", 1, "Descending")
                    .build();

    private static final GraphQLEnumType queryFilterOperatorEnum =new GraphQLEnumType("QueryFilterOperator",
            "查询过滤操作符", Arrays.stream(QueryFilterOperator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(),qfo.getDescription(),qfo.getValue())).collect(Collectors.toList()));

    private static final GraphQLEnumType queryFilterCombinatorEnum =new GraphQLEnumType("QueryFilterCombinator",
            "查询表达式组合操作符", Arrays.stream(QueryFilterCombinator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(),qfo.getDescription(),qfo.getValue())).collect(Collectors.toList()));


}
