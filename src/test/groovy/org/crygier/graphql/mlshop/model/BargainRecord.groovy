package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/8/27 8:55
 */

@Entity
@SchemaDocumentation("砍价记录")
@CompileStatic
@Bostype("A22")
class BargainRecord extends BosEntity {

    @SchemaDocumentation("订单信息")
    @ManyToOne
    Order order;

    @SchemaDocumentation("要求人数")
    Integer peopleNumber;

    @SchemaDocumentation("金额 单位分")
    Long amount;

    @SchemaDocumentation("有效时间")
    Long effectiveTime;

    @SchemaDocumentation("已砍金额")
    Long chopAmount = 0L;

    @SchemaDocumentation("已砍用户手机号")
    String chopPhone;

    @SchemaDocumentation("当前完成砍价人数")
    Integer chopCount = 0;


}
