package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2018/8/13 16:11
 */
@Entity
@SchemaDocumentation("交易记录")
@CompileStatic
@Bostype("A20")
public class TradeRecord extends BosEntity{

    @SchemaDocumentation("订单信息")
    @ManyToOne
    Order order;

    @SchemaDocumentation("客户信息")
    @ManyToOne
    Customer customer;

    @SchemaDocumentation("金额")
    Long amount;

    @SchemaDocumentation("转出账号")
    String turnOutAccount;

    @SchemaDocumentation("转入账号")
    String turnInAccount;



}
