package org.crygier.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionResult
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.model.users.Privi
import org.crygier.graphql.model.users.PriviGroup
import org.crygier.graphql.model.users.Role
import org.crygier.graphql.model.users.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@GRestController("ABC")
@RestController
@CompileStatic
class GraphQlController {

    @Autowired
    private GraphQLExecutor graphQLExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(path = '/graphql', method = RequestMethod.POST)
    ExecutionResult graphQl(@RequestBody final GraphQLInputQuery query) {
        Map<String, Object> variables = query.getVariables() ? objectMapper.readValue(query.getVariables(), Map) : null;

        return graphQLExecutor.execute(query.getQuery(), variables);
    }

    public static final class GraphQLInputQuery {
        String query;
        String variables;
    }

/**
 * Graphql
 * @param User
 * @return
 */
    @GRequestMapping(path = '/abc', method = RequestMethod.POST)
   @SchemaDocumentation("GraphQlController.create测试下行不行")
    User create(@RequestParam(name="role",required = true)Role role,@RequestParam(name="id",required = false)String id,@RequestParam(name="count",required = true)int count) {
        return null;
    }
}