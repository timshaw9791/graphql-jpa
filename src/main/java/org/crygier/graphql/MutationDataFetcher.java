package org.crygier.graphql;

import graphql.language.*;
import graphql.schema.*;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MutationDataFetcher implements DataFetcher {

    protected EntityManager entityManager;
    protected Method controllerMethod;

    public MutationDataFetcher(EntityManager entityManager, Method controllerMethod) {
        this.entityManager = entityManager;
        this.controllerMethod = controllerMethod;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {

        Field field=environment.getFields().iterator().next();
        //TODO get mutation name,argument type.
       // field.getArguments().stream()

        List<Argument> argument=field.getArguments();
        //argument.get(0).getValue()
        return null;

    }
}
