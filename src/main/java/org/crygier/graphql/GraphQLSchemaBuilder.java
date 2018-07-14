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
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
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
 *
 * Note: This class should not be accessed outside this library.
 */
public class GraphQLSchemaBuilder extends GraphQLSchema.Builder implements IGraphQlTypeMapper{

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginationRequest";
    public static final String QFILTER_REQUEST_PARAM_NAME = "qfilter";
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    public static final String MUTATION_INPUTTYPE_POSTFIX = "_";

    private final EntityManager entityManager;

    //似乎是output类别的基础类型（包括枚举）的一个缓存
    private final Map<Class, GraphQLType> classCache = new HashMap<>();

    private final Map<Class, GraphQLInputType> classInputCache = new HashMap<>();

    private final Map<EmbeddableType<?>, GraphQLObjectType> embeddableCache = new HashMap<>();
    private final Map<EntityType, GraphQLObjectType> entityCache = new HashMap<>();
    private final Map<EntityType, GraphQLInputObjectType> entityInputCache = new HashMap<>();
    private final List<AttributeMapper> attributeMappers = new ArrayList<>();

    private final Map<Method,Object> methodTargetMap=new HashMap<>();

    //可用的mutation返回类型DataFetcher.
    //private final Map<Class,DataFetcher> mutationReturnEntityClassDataFetcherMap=new HashMap<>();


    /**
     * Initialises the builder with the given {@link EntityManager} from which we immediately start to scan for
     * entities to include in the GraphQL schema.
     * @param entityManager The manager containing the data models to include in the final GraphQL schema.
     */
    public GraphQLSchemaBuilder(EntityManager entityManager, Collection<Object> controllerObjects) {
        this(entityManager,controllerObjects,null);
    }
    /**
     *
     * @param entityManager
     * @param controllerObjects  - 所有的对象必须都含有@GRestController注解
     * @param attributeMappers
     */
    public GraphQLSchemaBuilder(EntityManager entityManager,Collection<Object> controllerObjects, Collection<AttributeMapper> attributeMappers) {
        this.entityManager = entityManager;

        populateStandardAttributeMappers();
//初始化this.methodTargetMap
        controllerObjects.stream()
                .forEach(controllerObj -> {
                    Arrays.stream(controllerObj.getClass().getDeclaredMethods())
                            .forEach(method -> {
                                if (Arrays.stream(method.getAnnotations()).filter(annotation ->
                                        GRequestMapping.class.equals(annotation.annotationType()))
                                        .findFirst().isPresent()) {
                                    this.methodTargetMap.put(method, controllerObj);
                                }
                            });
                });

        GraphQLObjectType querytype=getQueryType();
        if(attributeMappers!=null){
            this.attributeMappers.addAll(attributeMappers);
        }
        Stream<GraphQLInputType> additionalTypeStream=this.getInputTypeStreamForMutation();
        //把在mutation中可能会用到的输入类型放进去，这与在查询中用到的参数输入类型有所不同，在名称上他们相差一个后缀，在结构上query参数类型只包含标量，而mutation参数类型则包含嵌套对象（除了parent属性之外）
        additionalTypeStream.forEach(additionalType->super.additionalType(additionalType));
        GraphQLObjectType mutationtype=getMutationType();

        super.query(querytype).mutation(mutationtype);
    }

    /**
     * 根据实体模型获取所有可能的mutationInputType。
     * @return
     */
    private Stream<GraphQLInputType> getInputTypeStreamForMutation() {

        //embeded类型的
        Stream<GraphQLInputType> embedGraphQLInputTypeStream=this.entityManager.getMetamodel().getEmbeddables().stream().filter(this::isNotIgnored)
                .filter(distinctByKey(o -> o.getJavaType()))//根据java类型去重
                .map(entityType -> {
            GraphQLInputObjectType inputObjectType = newInputObject()
                    .name(entityType.getJavaType().getSimpleName() + MUTATION_INPUTTYPE_POSTFIX)
                    .description(getSchemaDocumentation(entityType.getJavaType()))
                    .fields(entityType.getAttributes().stream()
                            .filter(attribute -> !"parent".equals(attribute.getName()))//去掉parent属性
                            .filter(this::isNotIgnored)//去掉忽略属性
                            .map(this::getInputObjectField)//根据字段属性获取对应字段。GraphQLInputTypeObjectFiled
                            .collect(Collectors.toList()))
                    .build();
            return inputObjectType;
        });
//实体类型的
        return Stream.concat(embedGraphQLInputTypeStream, this.entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored)
                .filter(distinctByKey(o -> o.getJavaType()))//根据java类型去重
                .map(entityType -> {
            GraphQLInputObjectType inputObjectType = newInputObject()
                    .name(entityType.getName() + MUTATION_INPUTTYPE_POSTFIX)
                    .description(getSchemaDocumentation(entityType.getJavaType()))
                    .fields(entityType.getAttributes().stream()
                            .filter(attribute -> !"parent".equals(attribute.getName()))//去掉parent属性
                            .filter(this::isNotIgnored)//去掉忽略属性
                            .map(this::getInputObjectField)//根据字段属性获取对应字段。GraphQLInputTypeObjectFiled
                            .collect(Collectors.toList()))
                    .build();
            //也添加到inputCache中
            entityInputCache.put(entityType, inputObjectType);
            return inputObjectType;
        }));

    }

    /**
     * 根据k来过滤的断言， TODO 可以挪到stream的工具中去
     * @param keyExtractor 拿到key的function
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    private void populateStandardAttributeMappers() {
        attributeMappers.add(createStandardAttributeMapper(UUID.class, JavaScalars.GraphQLUUID));
        attributeMappers.add(createStandardAttributeMapper(Date.class, JavaScalars.GraphQLDate));
        attributeMappers.add(createStandardAttributeMapper(LocalDateTime.class, JavaScalars.GraphQLLocalDateTime));
        attributeMappers.add(createStandardAttributeMapper(Instant.class, JavaScalars.GraphQLInstant));
        attributeMappers.add(createStandardAttributeMapper(LocalDate.class, JavaScalars.GraphQLLocalDate));
    }

    private AttributeMapper createStandardAttributeMapper(final Class<?> assignableClass, final GraphQLScalarType type) {
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
        GraphQLObjectType.Builder queryType = newObject().name("QueryType_JPA").description("All encompassing schema for this JPA environment");
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldDefinition).collect(Collectors.toList()));
        //TODO 要将所有的分录Entry排除,类型必须有，但不是顶级的，无法从此处开始查询数据，必须从顶级实体开始查询。
        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));
        //TODO 这个embedd类型的也可以先排除
        queryType.fields(entityManager.getMetamodel().getEmbeddables().stream().filter(this::isNotIgnored).map(this::getQueryEmbeddedFieldDefinition).collect(Collectors.toList()));
        return queryType.build();
    }


    private GraphQLObjectType getMutationType() {

        GraphQLObjectType.Builder queryType = newObject().name("Mutation").description("所有的mutation操作");
        queryType.fields(this.methodTargetMap.entrySet().stream().map(entry->{
            String grc=entry.getValue().getClass().getAnnotation(GRestController.class).value();
            String grm =  entry.getKey().getAnnotation(GRequestMapping.class).path()[0];
            String mutationFieldName = ("/" + grc + grm).replace("//", "/").replace("/", "_").substring(1);
            List<GraphQLArgument> gqalist = getMutationGraphQLArgumentsByMethod(entry.getKey());
            //DataFetcher mutationReturnTypeDataFetcher=getMutationReturnTypeDataFetcher(entry.getKey().getReturnType());

            EntityType entityType=entityManager.getMetamodel().getEntities().stream()
                    .filter(et->et.getJavaType().equals(entry.getKey().getReturnType())).findFirst().orElse(null);

            return newFieldDefinition()
                    .name(mutationFieldName)
                    .description(getSchemaDocumentation((AnnotatedElement)entry.getKey()))
                    .type(this.getMutationReturnType(entry.getKey().getReturnType()))
                    .dataFetcher(new MutationDataFetcher(entityManager,entityType, entry.getKey(),entry.getValue(),gqalist,this))
                    .argument(gqalist)
                    .build();
        }).collect(Collectors.toList()));
        return queryType.build();
    }


   /* private DataFetcher getMutationReturnTypeDataFetcher(Class<?> returnType) {
        //如果是基础类型
        if (this.getBasicAttributeType(returnType) != null) {
            return null;
            //如果是实体类型
        } else {
            DataFetcher result = this.mutationReturnEntityClassDataFetcherMap.get(returnType);
            if (result == null) {
                throw new RuntimeException("getMutationReturnTypeDataFetcher error!");
            } else {
                return result;
            }
        }
    }*/

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



    GraphQLFieldDefinition getQueryFieldDefinition(EntityType<?> entityType) {


        JpaDataFetcher jpaDataFetcher=new JpaDataFetcher(entityManager, entityType);

        //this.mutationReturnEntityClassDataFetcherMap.put(entityType.getJavaType(),jpaDataFetcher);

        return newFieldDefinition()
                .name(entityType.getName())
                .description(getSchemaDocumentation(entityType.getJavaType()))
                .type(getObjectType(entityType))
                .dataFetcher(jpaDataFetcher)
                .argument(entityType.getAttributes().stream().filter(this::isValidInput).filter(this::isNotIgnored).flatMap(this::getArgument).collect(Collectors.toList()))
                .build();
    }
    
    GraphQLFieldDefinition getQueryEmbeddedFieldDefinition(EmbeddableType<?> embeddableType) {
    	String embeddedName = embeddableType.getJavaType().getSimpleName();
        return newFieldDefinition()
                .name(embeddedName)
                .description(getSchemaDocumentation(embeddableType.getJavaType()))
                .type(new GraphQLList(getObjectType(embeddableType)))
                .argument(embeddableType.getAttributes().stream().filter(this::isValidInput).filter(this::isNotIgnored).flatMap(this::getArgument).collect(Collectors.toList()))
                .build();
    }
//查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = newObject()
                .name(entityType.getName() + "Connection")
                .description("'Connection' response wrapper object for " + entityType.getName() + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("The actual object results").type(new GraphQLList(getObjectType(entityType))).build())
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

    private GraphQLOutputType getMutationReturnType(Class<?> returnType) {
        return (GraphQLOutputType)getGraphQLTypeFromClassType(returnType);
    }

    private GraphQLInputType getGraphQLInputTypeFromClassType(Class typeClazz){
        GraphQLInputType graphQLInputType = entityInputCache.entrySet().stream().filter(entry -> entry.getKey().getJavaType().equals(typeClazz))
                .map(entry -> (GraphQLInputType)entry.getValue())
                .findFirst().orElseGet(()->getBasicAttributeInputType(typeClazz));
        if(graphQLInputType==null){
            throw new RuntimeException("error getArgumentFormParameter!");
        }
        return graphQLInputType;
    }

    private GraphQLType getGraphQLTypeFromClassType(Class typeClazz){
        GraphQLType graphQLObjectType=null;
        Optional<GraphQLObjectType> graphQLObjectTypeOptional=entityCache.entrySet().stream().filter(entry -> entry.getKey().getJavaType().equals(typeClazz))
                .map(entry -> entry.getValue())
                .findFirst();
        if(graphQLObjectTypeOptional.isPresent()){
            graphQLObjectType=graphQLObjectTypeOptional.get();
        }
        if (graphQLObjectType == null) {
            graphQLObjectType = getBasicAttributeType(typeClazz);
        }
        if(graphQLObjectType==null){
            throw new RuntimeException("error getArgumentFormParameter!");
        }
        return graphQLObjectType;
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

        //TODO 单独挪到一个初始化方法中去中去
        GraphQLObjectType answer = newObject()
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
        GraphQLObjectType answer = newObject()
                .name(embeddableName)
                .description(getSchemaDocumentation(embeddableType.getJavaType()))
                .fields(embeddableType.getAttributes().stream().filter(this::isNotIgnored).flatMap(this::getObjectField).collect(Collectors.toList()))
                .build();

        embeddableCache.put(embeddableType, answer);

        return answer;
    }

    private GraphQLInputObjectField getInputObjectField(Attribute attribute) {
                    return newInputObjectField()
                            .name(attribute.getName())
                            .description(getSchemaDocumentation(attribute.getJavaMember()))
                            .type(getAttributeInputType(attribute))
                            .build();
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


    private GraphQLInputType getBasicAttributeInputType(Class javaType) {
// First check our 'standard' and 'customized' Attribute Mappers.  Use them if possible
        Optional<AttributeMapper> customMapper = attributeMappers.stream()
                .filter(it -> it.getBasicAttributeType(javaType).isPresent())
                .findFirst();

        if (customMapper.isPresent())
            return customMapper.get().getBasicAttributeType(javaType).get();
        else if (String.class.isAssignableFrom(javaType))
            return GraphQLString;
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
            return getInputTypeFromJavaType(javaType);
        } else if (BigDecimal.class.isAssignableFrom(javaType)) {
            return Scalars.GraphQLBigDecimal;
        }

        throw new UnsupportedOperationException(
                "Class could not be mapped to GraphQL: '" + javaType.getClass().getTypeName() + "'");
    }

    /**
     * 找到基础类型的类型，包括枚举enum
     * @param javaType
     * @return 如果没找到，则回返回null
     */
    private GraphQLType getBasicAttributeType(Class javaType) {
        // First check our 'standard' and 'customized' Attribute Mappers.  Use them if possible
        Optional<AttributeMapper> customMapper = attributeMappers.stream()
                .filter(it -> it.getBasicAttributeType(javaType).isPresent())
                .findFirst();

        if (customMapper.isPresent())
            return customMapper.get().getBasicAttributeType(javaType).get();
        else if (String.class.isAssignableFrom(javaType))
            return GraphQLString;
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

        return null;
    }

    private GraphQLInputType getAttributeInputType(Attribute attribute) {

        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            try {
                return getBasicAttributeInputType(attribute.getJavaType());
            } catch (UnsupportedOperationException e) {
                //fall through to the exception below
                //which is more useful because it also contains the declaring member
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName()+ MUTATION_INPUTTYPE_POSTFIX));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName()+ MUTATION_INPUTTYPE_POSTFIX);
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            //TODO 因该不用了，因为我们的集合体现在manytoone中
            Type foreignType = ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(getInputTypeFromJavaType(foreignType.getJavaType()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
            EmbeddableType<?> embeddableType = (EmbeddableType<?>) ((SingularAttribute<?,?>) attribute).getType();
            return new GraphQLTypeReference(embeddableType.getJavaType().getSimpleName()+ MUTATION_INPUTTYPE_POSTFIX);
        }

        final String declaringType = attribute.getDeclaringType().getJavaType().getName(); // fully qualified name of the entity class
        final String declaringMember = attribute.getJavaMember().getName(); // field name in the entity class

        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + declaringMember + "' of entity class '" + declaringType + "'");

    }
    private Stream<GraphQLType> getAttributeType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLType graphQLType = getBasicAttributeType(attribute.getJavaType());
            if (graphQLType != null) {
                return Stream.of(graphQLType);
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            //这里的引用只能引用到GraphQLType而不可能是GraphInputQL类型
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

    private GraphQLInputType getInputTypeFromJavaType(Class clazz) {
        if (clazz.isEnum()) {
            if (classInputCache.containsKey(clazz))
                return classInputCache.get(clazz);

            GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(clazz.getSimpleName()+MUTATION_INPUTTYPE_POSTFIX);
            int ordinal = 0;
            for (Enum enumValue : ((Class<Enum>) clazz).getEnumConstants())
                enumBuilder.value(enumValue.name(), ordinal++);

            GraphQLInputType answer = enumBuilder.build();
            setIdentityCoercing(answer);

            classInputCache.put(clazz, answer);
            return answer;
        }

        return getBasicAttributeInputType(clazz);
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

    private static final GraphQLEnumType queryFilterOperatorEnum =new GraphQLEnumType("QueryFilterOperator",
            "查询过滤操作符", Arrays.stream(QueryFilterOperator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(),qfo.getDescription(),qfo.getValue())).collect(Collectors.toList()));

    private static final GraphQLEnumType queryFilterCombinatorEnum =new GraphQLEnumType("QueryFilterCombinator",
            "查询表达式组合操作符", Arrays.stream(QueryFilterCombinator.values())
            .map(qfo -> new GraphQLEnumValueDefinition(qfo.name(),qfo.getDescription(),qfo.getValue())).collect(Collectors.toList()));


    @Override
    public Class getClazzByInputType(GraphQLType graphQLType) {
        return this.entityInputCache.entrySet().stream().filter(entry->entry.getValue().equals(graphQLType)).map(entry->entry.getKey().getJavaType()).findFirst().orElse(null);

    }

    public GraphQLType getInputTypeByClass(Class<?> propertyType){
        GraphQLType gqtype=this.entityInputCache.entrySet().stream().filter(entry->entry.getKey().getJavaType().equals(propertyType)).map(entry->(GraphQLType)entry.getValue()).findFirst()
        .orElse((GraphQLType)this.classInputCache.get(propertyType));
        if(gqtype!=null){
            return gqtype;
        }else if(String.class.equals(propertyType)) {
            return Scalars.GraphQLString;
        }else if(String.class.equals(propertyType)) {
            return Scalars.GraphQLInt;
        }else{
            return null;
        }
    }
}
