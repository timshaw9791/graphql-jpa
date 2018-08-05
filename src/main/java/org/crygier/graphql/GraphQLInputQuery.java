package org.crygier.graphql;

import java.util.Map;

public final class GraphQLInputQuery {

    public GraphQLInputQuery(String query, Map<String, Object> arguments) {
        this.query = query;
        this.arguments = arguments;
    }

    private String query;

    private Map<String,Object> arguments;

    Map<String, Object> getArguments() {
        return arguments;
    }

    String getQuery() {
        return query;
    }
}