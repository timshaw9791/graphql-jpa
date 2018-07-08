package org.crygier.graphql;

import graphql.language.*;
import graphql.schema.*;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

public class JpaDataFetcher implements DataFetcher {

    protected EntityManager entityManager;
    protected EntityType<?> entityType;

    public JpaDataFetcher(EntityManager entityManager, EntityType<?> entityType) {
        this.entityManager = entityManager;
        this.entityType = entityType;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        return getQuery(environment, environment.getFields().iterator().next(),null).getResultList();
    }
    private void travelFieldSelection(CriteriaBuilder cb,Path root,SelectionSet selectionSet,List<Argument> arguments,List<Order> orders,EntityGraph entityGraph,Subgraph subgraph,boolean putonselect){
        if(selectionSet!=null){
            selectionSet.getSelections().forEach(selection -> {
                if (selection instanceof Field) {
                    Field selectedField = (Field) selection;
                    // "__typename" is part of the graphql introspection spec and has to be ignored by jpa
                    if (!"__typename".equals(selectedField.getName()) && !"parent".equals(selectedField.getName())) {
                        Path fieldPath = root.get(selectedField.getName());

                        // Process the orderBy clause
                        // TODO 排序如果出现在第二层会有一些问题，似乎没法影响到，似乎在指明one2many下分录的排序规则时，会碰到问题，可能跟entry以set形式出现有关系。many2one应该不会。
                        Optional<Argument> orderByArgument = selectedField.getArguments().stream().filter(it -> "orderBy".equals(it.getName())).findFirst();
                        if (orderByArgument.isPresent()) {
                            if ("DESC".equals(((EnumValue) orderByArgument.get().getValue()).getName())) {
                                orders.add(cb.desc(fieldPath));
                            } else {
                                orders.add(cb.asc(fieldPath));
                            }
                        }

                        //TODO 此处的指定的过滤参数似乎意义不大，不过留着吧。
                        // Process arguments clauses
                        arguments.addAll(selectedField.getArguments().stream()
                                .filter(it -> !"orderBy".equals(it.getName()))
                                .map(it -> new Argument(selectedField.getName() + "." + it.getName(), it.getValue()))
                                .collect(Collectors.toList()));

                        Path root2=null;
                        Subgraph subgraph2=null;
                        //找到对应的属性信息
                        Attribute selectedFieldAttribute = JpaDataFetcher.this.entityManager.getMetamodel().entity(root.getJavaType()).getAttribute(selectedField.getName());
                        //如果是one2many属性或者many2one，则直接用entitygraph中增加属性节点，以便join进来，
                        if ((selectedFieldAttribute instanceof PluralAttribute &&
                                ((PluralAttribute) selectedFieldAttribute).getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY)
                                ||(selectedFieldAttribute instanceof SingularAttribute
                                && ((SingularAttribute) selectedFieldAttribute).getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE)
                                ) {
                            //left outer join的形式加入
                            root2 = ((From) root).join(selectedField.getName(),JoinType.LEFT);
                            if (entityGraph != null ) {
                                subgraph2 = entityGraph.addSubgraph(selectedField.getName());
                            } else {
                                subgraph2 = subgraph.addSubgraph(selectedField.getName());
                            }
                        }else if(putonselect){
                            if(entityGraph!=null){
                                entityGraph.addAttributeNodes(selectedField.getName());
                            }else{
                                subgraph.addAttributeNodes(selectedField.getName());
                            }
                        }
                        //如果还有下一层，则需要递归。
                        if (((Field) selection).getSelectionSet() != null) {
                            travelFieldSelection(cb, root2, ((Field) selection).getSelectionSet(), arguments, orders, null,subgraph2,false);
                        }
                    }
                }
            });
        }
    }

    //TODO 修改本方法，以便ExtendJPADataFetcher加查询加条件
    protected TypedQuery getQuery(DataFetchingEnvironment environment, Field field, QueryFilter queryFilter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery((Class) entityType.getJavaType());
        Root root = query.from(entityType);

        List<Argument> arguments = new ArrayList<>();
        List<Order> orders=new ArrayList<>();
        EntityGraph graph = entityManager.createEntityGraph(entityType.getJavaType());

        // Loop through all of the fields being requested
        //迭代的形式以便组成一条语句
        travelFieldSelection(cb,root,field.getSelectionSet(),arguments,orders,graph,null,false);

        query.orderBy(orders);

        //最终将所有的非orderby形式的argument转化成predicate，并转成where子句，TODO 应该在返回后加上ExtendJpaDataFetcher里自带的过滤器filter生成的Predicate
        arguments.addAll(field.getArguments());
        List<Predicate> predicates = arguments.stream().map(it -> getPredicate(cb, root, environment, it)).collect(Collectors.toList());
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        //将entitygraph加入
        TypedQuery tquery=entityManager.createQuery(query.distinct(true));
        tquery.setHint("javax.persistence.fetchgraph", graph);
        return tquery;
    }

    private Predicate getPredicate(CriteriaBuilder cb, Root root, DataFetchingEnvironment environment, Argument argument) {
        Path path = null;
        if (!argument.getName().contains(".")) {
            Attribute argumentEntityAttribute = getAttribute(environment, argument);

            // If the argument is a list, let's assume we need to join and do an 'in' clause
            if (argumentEntityAttribute instanceof PluralAttribute) {
                Join join = root.join(argument.getName());
                return join.in(convertValue(environment, argument, argument.getValue()));
            }

            path = root.get(argument.getName());
            //TODO 默认用用了equal
            return cb.equal(path, convertValue(environment, argument, argument.getValue()));
        } else {
            List<String> parts = Arrays.asList(argument.getName().split("\\."));
            for (String part : parts) {
                if (path == null) {
                    path = root.get(part);
                } else {
                    path = path.get(part);
                }
            }

            return cb.equal(path, convertValue(environment, argument, argument.getValue()));
        }
    }

    protected Object convertValue(DataFetchingEnvironment environment, Argument argument, Value value) {
        if (value instanceof StringValue) {
            Object convertedValue =  environment.getArgument(argument.getName());
            if (convertedValue != null) {
                // Return real parameter for instance UUID even if the Value is a StringValue
                return convertedValue;
            } else {
                // Return provided StringValue
                return ((StringValue) value).getValue();
            }
        }
        else if (value instanceof VariableReference)
            return environment.getArguments().get(((VariableReference) value).getName());
        else if (value instanceof ArrayValue)
            return ((ArrayValue) value).getValues().stream().map((it) -> convertValue(environment, argument, it)).collect(Collectors.toList());
        else if (value instanceof EnumValue) {
            Class enumType = getJavaType(environment, argument);
            return Enum.valueOf(enumType, ((EnumValue) value).getName());
        } else if (value instanceof IntValue) {
            return ((IntValue) value).getValue();
        } else if (value instanceof BooleanValue) {
            return ((BooleanValue) value).isValue();
        } else if (value instanceof FloatValue) {
            return ((FloatValue) value).getValue();
        }

        return value.toString();
    }

    private Class getJavaType(DataFetchingEnvironment environment, Argument argument) {
        Attribute argumentEntityAttribute = getAttribute(environment, argument);

        if (argumentEntityAttribute instanceof PluralAttribute)
            return ((PluralAttribute) argumentEntityAttribute).getElementType().getJavaType();

        return argumentEntityAttribute.getJavaType();
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
        GraphQLType outputType = environment.getFieldType();
        if (outputType instanceof GraphQLList)
            outputType = ((GraphQLList) outputType).getWrappedType();

        if (outputType instanceof GraphQLObjectType)
            return (GraphQLObjectType) outputType;

        return null;
    }
}
