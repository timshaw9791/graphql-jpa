package org.crygier.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;

/**
 * @author Curtain
 * @date 2018/9/25 10:49
 */
public class CustomerGraphQlError implements GraphQLError {

    private final String message;
    private final List<Object> path;
    private final Throwable exception;
    private final List<SourceLocation> locations;
    private final Map<String, Object> extensions;

    public CustomerGraphQlError(List path, Throwable exception, List locations,String message) {
        this.path = path;
        this.exception = exception;
        this.locations = locations;
        this.extensions = null;
        this.message = message;
    }

    public Throwable getException() {
        return this.exception;
    }

    public String getMessage() {
        return this.message;
    }

    public List<SourceLocation> getLocations() {
        return this.locations;
    }

    public List<Object> getPath() {
        return this.path;
    }

    public Map<String, Object> getExtensions() {
        return this.extensions;
    }

    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    public String toString() {
        return "ExceptionWhileDataFetching{path=" + this.path + "exception=" + this.exception + "locations=" + this.locations + '}';
    }

    public boolean equals(Object o) {
        return GraphqlErrorHelper.equals(this, o);
    }

    public int hashCode() {
        return GraphqlErrorHelper.hashCode(this);
    }
}
