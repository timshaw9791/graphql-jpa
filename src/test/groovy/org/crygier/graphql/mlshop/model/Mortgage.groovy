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

//@Entity
//@SchemaDocumentation("公司按揭")
//@CompileStatic
//@Bostype("A13")
class Mortgage extends BosEntity{

    String company;

    String phone;

    String bank

    String customerName;

    String customerPhone;

    Long loanAmount;

    Double rate;

    Double rePointRate;

    Long rePointTime;

    @ManyToOne
    Salesman salesman;

    Long rePointAmount;

    Long signTime;

    Long loanTime;

    String mortgageAddress;

    String certificateImage;

    Double downPaymentRate;

    @SchemaDocumentation("月供:单位分")
    Long monthly;

    @SchemaDocumentation("期数/月")
    Long periods;


    Long accrual;






}
