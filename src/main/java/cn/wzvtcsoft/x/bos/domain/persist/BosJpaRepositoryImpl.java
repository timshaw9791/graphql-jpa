package cn.wzvtcsoft.x.bos.domain.persist;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.CoreObject;
import cn.wzvtcsoft.x.bos.domain.Entry;
import cn.wzvtcsoft.x.bos.domain.util.BosUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@NoRepositoryBean
public class BosJpaRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements Serializable {

    public BosJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public BosJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        if (entity instanceof BosEntity) {
            autoCreateNumberIfNecessary((BosEntity) entity);
            resetEntriesSeqAndParent(((CoreObject) entity));
        }
        return super.save(entity);
    }

    private void resetEntriesSeqAndParent(CoreObject coreObject) {

        List<Field> fieldlist = new ArrayList<Field>();
        Class clz = coreObject.getClass();
        while (!clz.equals(Object.class)) {
            Field[] fields = clz.getDeclaredFields();
            if (fields != null) {
                fieldlist.addAll(Arrays.asList(fields));
            }
            clz = clz.getSuperclass();
        }

        fieldlist.stream()
                .filter(field ->
                        (field.getAnnotation(OneToMany.class) != null)
                                && Set.class.isAssignableFrom(field.getType())
                )
                .forEach(field -> {
                    if (Entry.class.isAssignableFrom((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])) {
                        field.setAccessible(true);
                        Set<Entry> set = null;
                        try {
                            set = (Set<Entry>) field.get(coreObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new RuntimeException("CoreObject.resetEntriesSeqAndParent");
                        }
                        set.stream()
                                .forEach(entry -> {
                                    try {
                                        Class clazz = entry.getClass();
                                        while (!Entry.class.equals(clazz)) {
                                            clazz = clazz.getSuperclass();
                                        }
                                        Field parentField = clazz.getDeclaredField("parent");
                                        parentField.setAccessible(true);
                                        parentField.set(entry, coreObject);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot set parent value");
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot find parent field");
                                    }
                                    resetEntriesSeqAndParent(entry);
                                });
                    }
                });
    }

    private void autoCreateNumberIfNecessary(BosEntity entity) {
        if (!BosUtils.hasText(entity.getNumber())) {
            entity.setNumber("" + System.currentTimeMillis());
        }
    }

}

