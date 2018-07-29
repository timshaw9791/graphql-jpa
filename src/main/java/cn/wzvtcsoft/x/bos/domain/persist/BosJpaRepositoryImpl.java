package cn.wzvtcsoft.x.bos.domain.persist;

import cn.wzvtcsoft.x.bos.domain.CoreObject;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;

@NoRepositoryBean
public class BosJpaRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements  Serializable{

    public BosJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public BosJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        if(entity instanceof CoreObject){
            ((CoreObject)entity).resetEntriesSeqAndParent();
        }
        return super.save(entity);
    }
}

