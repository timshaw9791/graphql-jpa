package org.crygier.graphql;

import graphql.schema.GraphQLArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mutation方法调用到controller下的那些方法的各种元数据的封装
 */
public class MutationMethodMetaInfo {



    private Method properMethod;
    private Method proxyMethod;
    private Object target;
    private List<Annotation> validatorList;
    private List<AnnotatedGraphQLInputArgument> gqalist;


    public Method getProxyMethod() {
        return proxyMethod;
    }

    public Object getTarget() {
        return target;
    }

    List<GraphQLArgument> getGraphQLArgumentList(){
        return  this.gqalist.stream().map(gga->gga.getArgument()).collect(Collectors.toList());
    }

    public List<Annotation> getValidatorList() {
        return validatorList;
    }

    public List<AnnotatedGraphQLInputArgument> getGqalist() {
        return gqalist;
    }


    public MutationMethodMetaInfo(List<Annotation> validatorList, List<AnnotatedGraphQLInputArgument> gqalist,Method controllerMethod, Object target,Method properMethod) {
        this.validatorList = validatorList;
        this.gqalist = gqalist;
        this.proxyMethod =controllerMethod;
        this.target=target;
        this.properMethod=properMethod;
    }

    public final static class AnnotatedGraphQLInputArgument {
        private String name;
        private GraphQLArgument argument;
        private List<Annotation> validatorList;

        public AnnotatedGraphQLInputArgument(String name, GraphQLArgument argument, List<Annotation> validatorList) {
            this.name = name;
            this.argument = argument;
            this.validatorList = validatorList;
        }

        public String getName() {
            return name;
        }

        public GraphQLArgument getArgument() {
            return argument;
        }

        public List<Annotation> getValidatorList() {
            return validatorList;
        }
    }


    public Method getProperMethod() {
        return properMethod;
    }


}


