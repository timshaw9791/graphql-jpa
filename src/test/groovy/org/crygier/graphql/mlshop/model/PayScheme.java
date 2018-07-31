package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/7/30 10:48
 */
@Entity
@SchemaDocumentation("订单_支付方案")
@CompileStatic
@Bostype("A11")
public class PayScheme extends BosEntity{

    @SchemaDocumentation("进价 单位：分")
    Long purchasePrice;

    @SchemaDocumentation("原价 单位：分")
    Long oldPrice;

    @SchemaDocumentation("售价 单位: 分")
    Long price;

    @SchemaDocumentation("发票价 单位：分")
    Long receiptPrice;

    @SchemaDocumentation("快递费 单位：分")
    Long expressFee;

    @SchemaDocumentation("销售提成 单位：分")
    Long extract;

    @SchemaDocumentation("购置税(包) 单位：分")
    Long purchaseTax;

    @SchemaDocumentation("上牌费 单位：分")
    Long onCardFee;

    @SchemaDocumentation("服务费 单位：分")
    Long serviceFee;

    @SchemaDocumentation("发票高开补税 单位：分")
    Long taxCompensation;

    @SchemaDocumentation("购置税（客户） 单位：分")
    Long customerTax;
}
