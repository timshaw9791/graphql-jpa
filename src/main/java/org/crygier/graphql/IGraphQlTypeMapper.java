package org.crygier.graphql;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLType;

public interface IGraphQlTypeMapper {
    
    Class getClazzByInputType(GraphQLType graphQLType);

    /**
     * 根据类型得到inputType,可能是标量，也可以能是实体,枚举，嵌入型等所有类型
     * @param typeClazz
     * @return
     */
    GraphQLInputType getGraphQLInputTypeFromClassType(Class typeClazz);

}
