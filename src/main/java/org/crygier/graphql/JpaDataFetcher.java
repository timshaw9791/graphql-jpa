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


    public final Object get(DataFetchingEnvironment environment) {
        Object result=this.getResult(environment);
        //throw new CustomRuntimeException();
        //TODO 检查权限
        //checkPermission();
        return result;
    }


    public Object getResult(DataFetchingEnvironment environment) {
        TypedQuery typedQuery=getQuery(environment, environment.getFields().iterator().next(), null, false);
        Object result=typedQuery.getResultList().stream().findFirst().orElse(null);
        return result;
    }

    private void travelFieldSelection(CriteriaBuilder cb, Path root, SelectionSet selectionSet, List<Argument> arguments, List<Order> orders, EntityGraph entityGraph, Subgraph subgraph, boolean justforselectcount) {
        if (selectionSet != null) {
            selectionSet.getSelections().forEach(selection -> {
                if (selection instanceof Field) {
                    Field selectedField = (Field) selection;
                    String selectedFieldName = selectedField.getName();
                    // "__typename" is part of the graphql introspection spec and has to be ignored by jpa
                    if (!"__typename".equals(selectedFieldName) && !"parent".equals(selectedFieldName)) {
                        Path fieldPath = root.get(selectedField.getName());

                        // Process the orderBy clause
                        // TODO 排序如果出现在第二层会有一些问题，似乎没法影响到，似乎在指明one2many下分录的排序规则时，会碰到问题，可能跟entry以set形式出现有关系。many2one应该不会。

                        if (!justforselectcount) {
                            Optional<Argument> orderByArgument = selectedField.getArguments().stream().filter(it -> "orderBy".equals(it.getName())).findFirst();
                            if (orderByArgument.isPresent()) {
                                if ("DESC".equals(((EnumValue) orderByArgument.get().getValue()).getName())) {
                                    orders.add(cb.desc(fieldPath));
                                } else {
                                    orders.add(cb.asc(fieldPath));
                                }
                            }
                        }

                        // Process arguments clauses
                        arguments.addAll(selectedField.getArguments().stream()
                                .filter(it -> !"orderBy".equals(it.getName()))
                                .map(it -> new Argument(selectedFieldName + "." + it.getName(), it.getValue()))
                                .collect(Collectors.toList()));

                        Path root2 = joinIfNecessary((From) root, selectedFieldName);
                        Subgraph subgraph2 = null;
                        if (root2 != root && !justforselectcount) {
                            if (entityGraph != null) {
                                subgraph2 = entityGraph.addSubgraph(selectedField.getName());
                            } else {
                                subgraph2 = subgraph.addSubgraph(selectedField.getName());
                            }
                        }

                        //如果还有下一层，则需要递归。
                        if (((Field) selection).getSelectionSet() != null) {
                            travelFieldSelection(cb, root2, ((Field) selection).getSelectionSet(), arguments, orders, null, subgraph2, justforselectcount);
                        }
                    }
                }
            });
        }
    }

    /**
     * @param environment
     * @param field              -选择内容
     * @param queryFilter        - 过滤条件
     * @param justforselectcount - 是否仅仅为了查询符合条件的对象个数？
     * @return
     */
    protected TypedQuery getQuery(DataFetchingEnvironment environment, Field field, QueryFilter queryFilter, boolean justforselectcount) {


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = null;
        Root root = null;

        if (justforselectcount) {
            query = cb.createQuery(Long.class);
            root = query.from(entityType);
            SingularAttribute idAttribute = entityType.getId(Object.class);
            query.select(cb.countDistinct(root.get(idAttribute.getName())));
        } else {
            query = cb.createQuery((Class) entityType.getJavaType());
            root = query.from(entityType);
        }

        SelectionSet selectionSet = field.getSelectionSet();
        List<Argument> arguments = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        EntityGraph graph = entityManager.createEntityGraph(entityType.getJavaType());
        List<Predicate> predicates = new ArrayList<>();

        // Loop through all of the fields being requested
        //迭代的形式以便组成一条语句
        travelFieldSelection(cb, root, selectionSet, arguments, orders, graph, null, justforselectcount);

        Predicate predicatebyfilter = getPredicate(cb, root, environment, queryFilter);
        if (predicatebyfilter != null) {
            predicates.add(predicatebyfilter);
        }

        //最终将所有的非orderby形式的argument转化成predicate，并转成where子句.
        arguments.addAll(field.getArguments());
        final Root roottemp = root;
        predicates.addAll(arguments.stream().map(it -> getPredicate(cb, roottemp, environment, it)).collect(Collectors.toList()));
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        if (!justforselectcount) {
            query.orderBy(orders);
        }

        //将entitygraph加入
        return entityManager.createQuery(query.distinct(true)).setHint("javax.persistence.fetchgraph", graph);
    }

    private Predicate getPredicate(CriteriaBuilder cb, Root root, DataFetchingEnvironment environment, QueryFilter queryFilter) {
        if (queryFilter == null) {
            return null;
        }

        String k = queryFilter.getK(), v = queryFilter.getV();
        QueryFilterOperator qfo = queryFilter.getO();
        QueryFilterCombinator qfc = queryFilter.getAndor();


        List<String> parts = Arrays.asList(k.split("\\."));
        Path path = root;
        //TODO 这里要做报错处理，因为很可能数据导航写错了。
        for (String part : parts) {
            //如果(From)path不能转换，则说明queryfilter的k写错了。因为如果含有.那必须是形如roleItems.role.id这样的除最后一段外均为关系(可以作为From)的path
            From temp = joinIfNecessary((From) path, part);
            if (temp == path) {//如果没变动，说明到顶了，该属性为简单类型，拿到路径。
                path = temp.get(part);
            } else {
                path = temp;
            }
        }
        Predicate result = null;
        //TODO 需要进一步扩展
        switch (qfo) {
            case LIKE:
                result = cb.like(path, v);
                break;
            case ISNULL:
                result = cb.isNull(path);
                ;
                break;
            // case GREATTHAN:cb.greaterThan(path,)
            case EQUEAL:
                result = cb.equal(path, v);
                ;
                break;
        }
        //操作符没有，则直接返回
        if (qfc == null) {
            return result;
        }
        Predicate next = getPredicate(cb, root, environment, queryFilter.getNext());
        //如果下一个predicate本身为空，则也直接返回
        if (next == null) {
            return result;
        }

        switch (queryFilter.getAndor()) {
            case AND:
                return result = cb.and(result, next);
            case OR:
                return result = cb.or(result, next);
            // case NOT:
        }
        return result;

    }

    /**
     * @param currentjoin   -当前join
     * @param attributeName - 当前join对应的实体类中的某属性名称
     * @return
     */
    private From joinIfNecessary(From currentjoin, String attributeName) {
        //根据属性名拿到所在实体里对应的属性值，此处可能因为该属性不存在而报错，这说明queryfilter表达式写错了。
        Attribute selectedFieldAttribute = JpaDataFetcher.this.entityManager.getMetamodel().entity(currentjoin.getJavaType()).getAttribute(attributeName);
        //如果该属性是one2many或者many2one，则检查是否需要在当前from中添加join（如果之前存在就不添加，否则要添加）
        if ((selectedFieldAttribute instanceof PluralAttribute &&
                ((PluralAttribute) selectedFieldAttribute).getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY)
                || (selectedFieldAttribute instanceof SingularAttribute
                && ((SingularAttribute) selectedFieldAttribute).getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE)
                ) {

            Optional<Join> optjoin = currentjoin.getJoins().stream().filter(join -> attributeName.equals(((Join) join).getAttribute().getName())).findFirst();
            //如果不存在则添加,并将返回的from设置为新的join
            return optjoin.isPresent() ? optjoin.get() : currentjoin.join(attributeName, JoinType.LEFT);
        } else {//如果为简单类型，则说明用不到新的join，返回旧有的。
            return currentjoin;
        }
    }

    /**
     * @param cb
     * @param root
     * @param environment
     * @param argument    -为某个字段的过滤参数生成断言
     * @return
     */
    private Predicate getPredicate(CriteriaBuilder cb, Root root, DataFetchingEnvironment environment, Argument argument) {
        Path path = null;
        if (!argument.getName().contains(".")) {
            Attribute argumentEntityAttribute = getAttribute(environment, argument);
            //似乎只能采用但字段不带点号的，且不带关系的标量==式断言，因此这里的join意义不大，TODO 总觉得这个函数有点问题。
            // If the argument is a list, let's assume we need to join and do an 'in' clause
            if (argumentEntityAttribute instanceof PluralAttribute) {
                Join join = root.join(argument.getName());
                return join.in(convertValue(environment, argument, argument.getValue()));
            }

            path = root.get(argument.getName());
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
            Object convertedValue = environment.getArgument(argument.getName());
            if (convertedValue != null) {
                // Return real parameter for instance UUID even if the Value is a StringValue
                return convertedValue;
            } else {
                // Return provided StringValue
                return ((StringValue) value).getValue();
            }
        } else if (value instanceof VariableReference)
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
