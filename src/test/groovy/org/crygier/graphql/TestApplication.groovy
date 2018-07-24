package org.crygier.graphql

import com.aliyun.ossdemo.AliyunSTSController
import groovy.transform.CompileStatic
import org.crygier.graphql.GraphQLExecutor
import org.crygier.graphql.mlshop.controller.BusinessController
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@EnableAutoConfiguration
@EntityScan
@CompileStatic
class TestApplication {

    public static void main(String[] args) {
        ApplicationContext ac =SpringApplication.run(TestApplication.class,args);
    }

    @Bean
    @DependsOn(["GraphQlController","BusinessController"])
    public GraphQLExecutor graphQLExecutor(@Autowired ListableBeanFactory listableBeanFactory) {

        return new GraphQLExecutor( listableBeanFactory);
    }

    @Bean("GraphQlController")
    public GraphQlController() {
        return new GraphQlController();
    }
    @Bean("BusinessController")
    public BusinessController() {
        return new BusinessController();
    }

    @Bean("AliyunSTSController")
    public AliyunSTSController() {
        return new AliyunSTSController();
    }

}

