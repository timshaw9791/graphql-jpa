package cn.wzvtcsoft.x.bos.domain;


import cn.wzvtcsoft.x.bos.domain.CoreObject;
import cn.wzvtcsoft.x.bos.domain.persist.EntryParentType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

/**
 * Created by liutim on 2017/11/25.
 */


@MappedSuperclass
@Access(AccessType.FIELD)
@TypeDef(name = "EntryParentType", typeClass = EntryParentType.class)
public abstract class Entry extends CoreObject implements IEntry {


    @Column(name = "parent_id", length = 25)
    @Type(type = "EntryParentType")
    private ICoreObject parent;

    /*final protected ICoreObject getParent() {
        return parent;
    }*/
}
