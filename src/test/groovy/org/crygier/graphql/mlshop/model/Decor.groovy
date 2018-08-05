package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import cn.wzvtcsoft.x.bos.domain.Entry
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity


/**
 * @author Curtain
 * @date 2018/7/30 11:09
 */

@Entity
@SchemaDocumentation("装潢")
@CompileStatic
@Bostype("A14")
class Decor extends Entry{

    @SchemaDocumentation("客户姓名")
    String customerName;

    @SchemaDocumentation("销售员")
    String salesman;

    @SchemaDocumentation("装潢员")
    String upholstery;

    @SchemaDocumentation("门店")
    String shopName;

    @SchemaDocumentation("总成本")
    Long cost;

    @SchemaDocumentation("加装物品")
    String goods;

    @SchemaDocumentation("加装费用")
    Long goodsRecharge;

}
