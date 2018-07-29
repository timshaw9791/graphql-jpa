package org.crygier.graphql;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;

/**
 * 某个mutation执行过程中涉及到的元数据接口
 */
public interface MutationMetaInfo {

    /**
     * 这个mutation对应的返回类型
     * @return
     */
    GraphQLOutputType getGraphQLOutputType();

    /**
     * 所有的输入参数
     * @return
     */
    List<GraphQLArgument> getGraphQLArgumentList();

    /**
     * 根据参数名获取参数类型
     * @param name
     * @return
     */
    GraphQLArgument getGraphQLArgument(String name);

    /**
     * 获取返回值的EntityType，必须存在
     * @return
     */
    EntityType getEntityType();

    /**
     * 这个mutation对应的filedName
     * @return
     */
    String getMutationFieldName();

    /**
     * 根据实际参数执行真正的调用，包括设置参数默认值，validator验证，最终执行并返回。
     * @param nameArgMaps - 参数名-实际参数
     * @return
     */
    Object invoke(Map<String, Object> nameArgMaps);


}
