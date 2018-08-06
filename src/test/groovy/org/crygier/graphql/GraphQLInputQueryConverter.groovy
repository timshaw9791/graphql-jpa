package org.crygier.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import org.codehaus.groovy.util.StringUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;


@Component("graphQLInputQueryConverter")
public class GraphQLInputQueryConverter extends AbstractHttpMessageConverter<GraphQlController.GraphQLInputQuery> {
    @Autowired
    private ObjectMapper objectMapper;

    public GraphQLInputQueryConverter() {
        super(MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return GraphQlController.GraphQLInputQuery.class.isAssignableFrom(clazz);
    }

    @Override
    protected GraphQlController.GraphQLInputQuery readInternal(Class<? extends GraphQlController.GraphQLInputQuery> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        String requestBody = toString(inputMessage.getBody());
        Map<String,String> map = objectMapper.readValue(requestBody, Map.class);
        Map<String,Object> arguments=StringUtils.hasText(map.get("variables"))?objectMapper.readValue(map["variables"],Map.class):null;
        return new GraphQlController.GraphQLInputQuery(map.get("query"),arguments);
    }

    @Override
    protected void writeInternal(GraphQlController.GraphQLInputQuery report, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new RuntimeException("never happened !");
    }

    private static String toString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        return scanner.useDelimiter("\\A").next();
    }
}