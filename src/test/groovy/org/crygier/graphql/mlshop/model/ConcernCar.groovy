package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/8/1 15:18
 */

@Entity
@SchemaDocumentation("车辆信息")
@CompileStatic
@Bostype("A16")
class ConcernCar extends BosEntity{

    @SchemaDocumentation("客户")
    @ManyToOne
    Customer customer;

    @SchemaDocumentation("汽车信息")
    @ManyToOne
    CarInfo carInfo;
}
