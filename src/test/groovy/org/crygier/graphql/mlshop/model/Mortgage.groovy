package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/7/30 11:06
 */

@Entity
@SchemaDocumentation("公司按揭")
@CompileStatic
@Bostype("A13")
class Mortgage extends BosEntity{

    @SchemaDocumentation("4S店")
    String company;

    @SchemaDocumentation("4S电话")
    String phone;

    @SchemaDocumentation("银行")
    String bank

    @SchemaDocumentation("客户姓名")
    String customerName;

    @SchemaDocumentation("客户电话")
    String customerPhone;

    @SchemaDocumentation("贷款金额")
    Long loanAmount;

    @SchemaDocumentation("利率")
    BigDecimal rate;

    @SchemaDocumentation("返点利率")
    BigDecimal rePointRate;

    @SchemaDocumentation("返点时间")
    Long rePointTime;

    @SchemaDocumentation("销售员")
    String salesman;

    @SchemaDocumentation("返点金额")
    Long rePointAmount;

    @SchemaDocumentation("签订时间")
    Long signTime;

    @SchemaDocumentation("放款时间")
    Long loanTime;

    @SchemaDocumentation("签单地址")
    String mortgageAddress;

    @SchemaDocumentation("首付比率")
    BigDecimal downPaymentRate;

    @SchemaDocumentation("月供:单位分")
    Long monthly;

    @SchemaDocumentation("期数/月")
    Long periods;

    @SchemaDocumentation("利息")
    Long accrual;

    @SchemaDocumentation("首付金额")
    Long downPayments;

    @SchemaDocumentation("押金")
    Long deposit;

    @SchemaDocumentation("登记证书")
    String registrationCertificate;

    @SchemaDocumentation("手续费")
    Long serviceCharge;

    @SchemaDocumentation("GPS费")
    Long GPSCharge;

    @SchemaDocumentation("签单费")
    Long signBill;

    @SchemaDocumentation("签单返点费")
    Long signRePointBill;








}
