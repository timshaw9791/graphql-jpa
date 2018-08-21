package org.crygier.graphql

import cn.wzvtcsoft.x.bos.domain.persist.BosJpaRepositoryImpl
import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableAutoConfiguration
@EntityScan
@CompileStatic
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(repositoryBaseClass = BosJpaRepositoryImpl.class)
@EnableScheduling
class TestApplication {

    public static void main(String[] args) {
        ApplicationContext ac =SpringApplication.run(TestApplication.class,args);
    }

}

