package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.Bostype
import cn.wzvtcsoft.x.bos.domain.Entry
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/7/30 11:03
 */
@Entity
@SchemaDocumentation("订单保险续保信息集合")
@CompileStatic
@Bostype("A12")
class InsuranceItems extends Entry{

    @ManyToOne(fetch =FetchType.LAZY)
    Insurance insurance;
}
