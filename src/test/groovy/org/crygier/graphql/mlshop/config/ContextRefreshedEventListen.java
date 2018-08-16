package org.crygier.graphql.mlshop.config;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/16 9:06
 */

@Component
public class ContextRefreshedEventListen {
    @Autowired
    private ListableBeanFactory listableBeanFactory;

    Repositories repositories;

    @SuppressWarnings("rawtypes")
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        repositories = new Repositories(listableBeanFactory);

//        Iterator<Class<?>> it = repositories.iterator();
//        while (it.hasNext()) {
//            Class<?> domainClass = it.next();
//            Repository repository = (Repository) repositories.getRepositoryFor(domainClass).get();
//            System.out.println("The domain of the repository [" + repository + "] is : " + domainClass);
//        }
    }

    public Repository getRepository(Class<?> c) {

        Optional<Object> optional = repositories.getRepositoryFor(c);
        if (optional.isPresent()){
            Repository repository = (Repository) optional.get();
            System.out.println("The domain of the repository [" + repository + "] is : " + c);
            return repository;
        }
            throw new RuntimeException("not find repository");


    }
}
