package cn.wzvtcsoft.x.bos.domain;


import cn.wzvtcsoft.x.bos.domain.Entry;
import cn.wzvtcsoft.x.bos.domain.ICoreObject;
import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.event.spi.MergeEventListener;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by liutim on 2017/11/25.
 */


@MappedSuperclass
@Access(AccessType.FIELD)
public class CoreObject implements ICoreObject, Serializable {
    private String id;
    @Id
    @GeneratedValue(generator = "bosidgenerator")
    @GenericGenerator(name = "bosidgenerator", strategy = "cn.wzvtcsoft.x.bos.domain.persist.BosidGenerator")
    @Column(name = "id", nullable = false, length = 25)
    @Access(AccessType.PROPERTY)
    public String getId() {
        return this.id;
    }

    private void setId(String id) {
        this.id = id;
    }


    //version 乐观锁 TODO

    @Override
    final public boolean equals(Object obj) {
        if (this.id == null || obj == null || !(obj instanceof ICoreObject)) {
            return false;
        } else {
            return Objects.equals(this.id, ((ICoreObject) obj).getId());
        }
    }

    @Override
    final public int hashCode() {
        return (this.id == null) ? 13 : Objects.hash(this.id);
    }


    final public void resetEntriesSeqAndParent() {

        List<Field> fieldlist = new ArrayList<Field>();
        Class clz = this.getClass();
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
                            set = (Set<Entry>) field.get(this);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new RuntimeException("CoreObject.resetEntriesSeqAndParent");
                        }
                        set.stream()
                                .forEach(entry -> {
                                    try {
                                        Class clazz=entry.getClass();
                                        while (!Entry.class.equals(clazz)){
                                            clazz=clazz.getSuperclass();
                                        }
                                        Field parentField = clazz.getDeclaredField("parent");
                                        parentField.setAccessible(true);
                                        parentField.set(entry, this);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot set parent value");
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("CoreObject.resetEntriesSeqAndParent.cannot find parent field");
                                    }
                                    entry.resetEntriesSeqAndParent();
                                });
                    }
                });
    }

    ;
}
