package org.crygier.graphql;

import graphql.language.*;
import graphql.schema.DataFetchingEnvironment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class CollectionJpaDataFetcher extends JpaDataFetcher {

    public CollectionJpaDataFetcher(EntityManager entityManager, EntityType<?> entityType,IGraphQlTypeMapper graphQlTypeMapper) {
        super(entityManager, entityType,graphQlTypeMapper);
    }

    @Override
    public Object getResult(DataFetchingEnvironment environment) {
        Field field = environment.getFields().iterator().next();
        Map<String, Object> result = new LinkedHashMap<>();

        QueryFilter qfilter=extractQfilterInformation(environment,field);
        Paginator pageInformation = extractPageInformation(environment, field);

        // See which fields we're requesting
        Optional<Field> totalPagesSelection = getSelectionField(field, "totalPages");
        Optional<Field> totalElementsSelection = getSelectionField(field, "totalElements");
        Optional<Field> contentSelection = getSelectionField(field, "content");

        if (contentSelection.isPresent())
            result.put("content", getQueryForEntity(environment, qfilter, contentSelection.get(), false).setMaxResults(pageInformation.getSize()).setFirstResult((pageInformation.getPage() - 1) * pageInformation.getSize()).getResultList());

        if (totalElementsSelection.isPresent() || totalPagesSelection.isPresent()) {
            final Long totalElements = contentSelection
                    .map(contentField -> getCountQuery(environment, contentField,qfilter).getSingleResult())
                    // if no "content" was selected an empty Field can be used
                    .orElseGet(() -> getCountQuery(environment, new Field(),qfilter).getSingleResult());

            result.put("totalElements", totalElements);
            result.put("totalPages", ((Double) Math.ceil(totalElements / (double) pageInformation.getSize())).longValue());
        }

        return result;
    }

    /**
     *用来方便继承的
     * @param environment
     * @param qfilter - 过滤条件
     * @param field -字段信息
     * @param justforselectcount - 是否仅仅查询数量，
     * @return 如果仅仅查询数量则返回TypedQuery<Long>、如果查询的是Entity，则返回TypedQuery<EntityType>
     */
    protected TypedQuery getQueryForEntity(DataFetchingEnvironment environment, QueryFilter qfilter, Field field, boolean justforselectcount) {
        return super.getQuery(environment, field, qfilter, justforselectcount);
    }
    //用来方便继承的。
    protected Object getForEntity(DataFetchingEnvironment environment) {
        return super.getResult(environment);
    }

    private TypedQuery<Long> getCountQuery(DataFetchingEnvironment environment, Field field,QueryFilter qfilter) {
        return getQueryForEntity(environment, qfilter, field, true);
    }

    private Optional<Field> getSelectionField(Field field, String fieldName) {
        return field.getSelectionSet().getSelections().stream().filter(it -> it instanceof Field).map(it -> (Field) it).filter(it -> fieldName.equals(it.getName())).findFirst();
    }

    private Paginator extractPageInformation(DataFetchingEnvironment environment, Field field) {
        Optional<Argument> paginationRequest = field.getArguments().stream().filter(it -> GraphQLSchemaBuilder.PAGINATION_REQUEST_PARAM_NAME.equals(it.getName())).findFirst();
        if (paginationRequest.isPresent()) {
            field.getArguments().remove(paginationRequest.get());
            Value v=paginationRequest.get().getValue();
            return (Paginator)this.convertValue(environment,this.graphQlTypeMapper.getGraphQLInputTypeFromClassType(Paginator.class),v);
        }
        return new Paginator(1, Integer.MAX_VALUE);
    }



    private QueryFilter extractQfilterInformation(DataFetchingEnvironment environment, Field field) {
        Optional<Argument> qfilterRequest = field.getArguments().stream().filter(it -> GraphQLSchemaBuilder.QFILTER_REQUEST_PARAM_NAME.equals(it.getName())).findFirst();
        if (qfilterRequest.isPresent()) {
            field.getArguments().remove(qfilterRequest.get());
            ObjectValue qfilterValues = (ObjectValue) qfilterRequest.get().getValue();
            return (QueryFilter) this.convertValue(environment,this.graphQlTypeMapper.getGraphQLInputTypeFromClassType(QueryFilter.class),qfilterValues);
        }else{
            return null;
        }
    }

}
