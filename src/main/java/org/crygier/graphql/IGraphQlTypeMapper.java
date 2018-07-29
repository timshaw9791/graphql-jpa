package org.crygier.graphql;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Type;

interface IGraphQlTypeMapper {
    
    Class getClazzByInputType(GraphQLType graphQLType);

    /**
     * 根据类型得到inputType,可能是标量，也可以能是实体,枚举，嵌入型等所有类型
     * @param typeClazz
     * @return
     */
    GraphQLInputType getGraphQLInputTypeFromClassType(Class typeClazz);


     EntityType getEntityType(Class type);

    GraphQLOutputType getGraphQLOutputType(ManagedType<?> managedType);
}
