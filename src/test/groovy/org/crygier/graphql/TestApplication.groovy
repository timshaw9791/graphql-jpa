package org.crygier.graphql

import com.aliyun.ossdemo.AliyunSTSController
import groovy.transform.CompileStatic
import org.crygier.graphql.GraphQLExecutor
import org.crygier.graphql.mlshop.controller.BusinessController
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableAutoConfiguration
@EntityScan
@CompileStatic
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
class TestApplication {

    public static void main(String[] args) {
        ApplicationContext ac =SpringApplication.run(TestApplication.class,args);
    }
/*
    @Bean
    public GraphQLExecutor graphQLExecutor() {
        return new GraphQLExecutor();
    }

    @Bean
    public GraphQlController() {
        return new GraphQlController();
    }
    @Bean
    public BusinessController() {
        return new BusinessController();
    }
*/
    @Bean
    public AliyunSTSController() {
        return new AliyunSTSController();
    }


}

