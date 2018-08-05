package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.IEntity;
import graphql.language.*;
import graphql.schema.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

public class MutationDataFetcher extends CollectionJpaDataFetcher {

    protected MutationMetaInfo mutationMetaInfo = null;

    public MutationDataFetcher(EntityManager entityManager, IGraphQlTypeMapper graphQlTypeMapper, MutationMetaInfo mutationMetaInfo) {
        super(entityManager, mutationMetaInfo.getEntityType(), graphQlTypeMapper);
        this.mutationMetaInfo = mutationMetaInfo;
    }

    @Override
    public Object getResult(DataFetchingEnvironment environment,QueryFilter queryFilter) {
        Field field = environment.getFields().iterator().next();
        Map<String, Object> nameArgMaps = field.getArguments().stream()
                //过滤掉不需要的输入参数。
                .filter(realArg -> this.mutationMetaInfo.getGraphQLArgument(realArg.getName()) != null)
                .map(realArg ->
                        new AbstractMap.SimpleEntry<String, Object>(realArg.getName(), convertValue(environment,
                                (GraphQLInputType) this.mutationMetaInfo.getGraphQLArgument(realArg.getName()).getType(),
                                realArg.getValue()))
                ).collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
        Object returnValue = this.mutationMetaInfo.invoke(nameArgMaps);
        //说明返回的是简单类型
        if (this.entityType == null) {
            return returnValue;
        } else if (returnValue == null) {
            return null;
        } else {
            if (IEntity.class.isAssignableFrom(returnValue.getClass())) {//拿到主键
                String id = ((IEntity) returnValue).getId();
                queryFilter=new QueryFilter("id", QueryFilterOperator.EQUEAL, id, QueryFilterCombinator.AND, queryFilter);
                return super.getForEntity(environment,queryFilter);
            } else if (Collection.class.isAssignableFrom(returnValue.getClass())) {
                //设置分页和过滤条件
                return super.getResult(environment,null);
            } else {
                //TODO 只能抛错
                throw new RuntimeException("返回类型不对头，mutation中不允许出现该类型");
            }
        }
    }


}
