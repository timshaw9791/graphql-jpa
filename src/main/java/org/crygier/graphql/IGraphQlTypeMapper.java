package org.crygier.graphql;

import graphql.schema.GraphQLType;

public interface IGraphQlTypeMapper {
    
    Class getClazzByInputType(GraphQLType graphQLType);

    /**
     * 根据类型得到inputType,可能是标量，也可以能是实体
     * @param propertyType
     * @return
     */
    GraphQLType getInputTypeByClass(Class<?> propertyType);
}
