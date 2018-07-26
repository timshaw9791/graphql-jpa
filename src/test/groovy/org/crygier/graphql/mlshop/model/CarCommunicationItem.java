package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.Bostype;
import cn.wzvtcsoft.x.bos.domain.Entry;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author Curtain
 * @date 2018/7/26 9:53
 */

@Entity
@SchemaDocumentation("买车沟通记录")
@CompileStatic
@Bostype("B04")
public class CarCommunicationItem extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    CarCommunicationRecord carCommunicationRecord;
}
