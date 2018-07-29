package cn.wzvtcsoft.x.bos.domain;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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


    ;
}
