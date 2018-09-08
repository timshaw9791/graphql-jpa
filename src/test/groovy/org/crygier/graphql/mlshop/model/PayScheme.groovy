package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.Bostype
import cn.wzvtcsoft.x.bos.domain.Entry
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
/**
 * @author Curtain
 * @date 2018/7/30 10:48
 */
@Entity
@SchemaDocumentation("订单_支付方案")
@CompileStatic
@Bostype("A15")
public class PayScheme extends Entry{

    @SchemaDocumentation("进价 单位：分")
    String purchasePrice;

    @SchemaDocumentation("原价 单位：分")
    String oldPrice;

    @SchemaDocumentation("售价 单位: 分")
    Long price;

    @SchemaDocumentation("发票价 单位：分")
    String receiptPrice;

    @SchemaDocumentation("快递费 单位：分")
    String expressFee;

    @SchemaDocumentation("销售提成 单位：分")
    String extract;

    @SchemaDocumentation("购置税(包) 单位：分")
    String purchaseTax;

    @SchemaDocumentation("上牌费 单位：分")
    String onCardFee;

    @SchemaDocumentation("服务费 单位：分")
    String serviceFee;

    @SchemaDocumentation("发票高开补税 单位：分")
    String taxCompensation;

    @SchemaDocumentation("购置税（客户） 单位：分")
    String customerTax;

    @SchemaDocumentation("GPS费")
    String gpsCharge;

    @SchemaDocumentation("押金")
    String deposit;

    @SchemaDocumentation("业务员提成")
    String salesmanExtract;

    @SchemaDocumentation("手续费  单位：分")
    String serviceCharge;
}
