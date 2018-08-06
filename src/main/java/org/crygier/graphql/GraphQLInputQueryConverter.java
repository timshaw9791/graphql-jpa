package org.crygier.graphql;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import graphql.language.*;
import graphql.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用来
 */
@Component("graphQLInputQueryConverter")
public class GraphQLInputQueryConverter extends AbstractHttpMessageConverter<GraphQLInputQuery> {
    @Autowired
    private ObjectMapper objectMapper;

    public GraphQLInputQueryConverter() {
        super(MediaType.APPLICATION_JSON_UTF8);
    }

    @PostConstruct
    void init() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(GraphQLInputQuery.class, new GraphQLStringDeserializer());
        this.objectMapper.registerModule(module);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return GraphQLInputQuery.class.isAssignableFrom(clazz);
    }

    @Override
    protected GraphQLInputQuery readInternal(Class<? extends GraphQLInputQuery> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return objectMapper.readValue(toString(inputMessage.getBody()), GraphQLInputQuery.class);
    }

    private static String toString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        return scanner.useDelimiter("\\A").next();
    }

    @Override
    protected void writeInternal(GraphQLInputQuery report, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new RuntimeException("never happened !");
    }

    @Autowired
    IGraphQLExecutor graphQLExecutor;

    private class GraphQLStringDeserializer extends JsonDeserializer<GraphQLInputQuery> {


        @Override
        public GraphQLInputQuery deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec oc = jp.getCodec();
            JsonNode node = oc.readTree(jp);
            final String query = unwrap(node.get("query").asText());
            final String variables = unwrap(!node.has("variables") ? null : node.get("variables") != null ? node.get("variables").toString() : null);


            Map<String, Object> map = StringUtils.hasText(variables) ? objectMapper.readValue(variables, Map.class) : null;
            List<VariableDefinition> variableDefinitionList = graphQLExecutor.getOperationDefinition(query).getVariableDefinitions();
            Map<String, Object> argmentsMap = new HashMap<>();
            variableDefinitionList.stream().forEach(variableDefinition -> {
                GraphQLType graphQLType = graphQLExecutor.getGraphQLType(variableDefinition.getType());
                Object value = map.get(variableDefinition.getName());
                Object result = coerceValue(graphQLType, value);
                argmentsMap.put(variableDefinition.getName(),result);
            });
            return new GraphQLInputQuery(query, argmentsMap);
        }

        private Object coerceValue(GraphQLType graphQLType, Object value) {
            Object result = null;
            if (graphQLType instanceof GraphQLNonNull) {
                result = coerceValue(((GraphQLNonNull) graphQLType).getWrappedType(), value);
            } else if (value == null) {
                return null;
            } else if (graphQLType instanceof GraphQLScalarType) {
                result = set.contains(value.getClass()) ? value : value.toString();
            } else if (graphQLType instanceof GraphQLEnumType) {
                result = graphQLExecutor.getGraphQlTypeMapper().getBosEnumByValue((GraphQLEnumType) graphQLType, value.toString());
            } else if (graphQLType instanceof GraphQLList) {
                try {
                    if (value instanceof List) {
                        result = new ArrayList((List) value);
                    } else {
                        Collection<Object> objCollection = ((value instanceof Map) ? ((Map) value).values() : objectMapper.readValue(value.toString(), List.class));
                        result = objCollection.stream().map(
                                val -> coerceValue(((GraphQLList) graphQLType).getWrappedType(), val)
                        ).collect(Collectors.toList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("GraphQLStringDeserializer.coerceValue error");
                }
            } else if (graphQLType instanceof GraphQLInputObjectType) {
                final Map<String, Object> tempvalue;
                try {
                    tempvalue = (value instanceof Map) ? ((Map) value) : objectMapper.readValue(value.toString(), Map.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("GraphQLStringDeserializer.coerceValue error");
                }

                result = ((GraphQLInputObjectType) graphQLType).getFields().stream().filter(field -> tempvalue.containsKey(field.getName())).map(field ->
                        new AbstractMap.SimpleEntry<String, Object>(field.getName(), coerceValue(field.getType(), tempvalue.get(field.getName()))
                        )).collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
            }
            return result;
        }

        /**
         * 去掉多余的左右引号,以及内部的转义符号，相当于把多一层引号包裹去掉。
         *
         * @param str
         * @return
         */
        private String unwrap(String str) {
            if (StringUtils.hasText(str)) {
                if (str.startsWith("\"") && str.endsWith("\"") && str.length() >= 2) {
                    return str.substring(1, str.length() - 1).replace("\\\"", "\"").replace("\\n", "");
                } else {
                    return str;
                }
            } else {
                return null;
            }
        }

        final Set<Class> set = new HashSet(Arrays.asList(new Object[]{Integer.class, BigInteger.class, Boolean.class, String.class, Float.class, Double.class, BigDecimal.class, Long.class}));
    }
}