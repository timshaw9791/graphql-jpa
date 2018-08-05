package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.springframework.lang.Nullable;

import javax.persistence.metamodel.EntityType;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;

interface IGraphQlTypeMapper {

    @Nullable
    Class getClazzByInputType(GraphQLType graphQLType);

    @Nullable
    EntityType getEntityType(@NotNull  Class type);

    GraphQLOutputType getGraphQLOutputType(Type type);

    GraphQLInputType getGraphQLInputType(Type type);

    /**
     * 获取某个实体类对应list的输出类型
     * @param entityType
     * @return
     */
    String getGraphQLTypeNameOfEntityList(EntityType entityType);
//TODO 似乎要抛异常！
    BosEnum getBosEnumByValue(GraphQLEnumType bosEnumType, String enumValue);
}
