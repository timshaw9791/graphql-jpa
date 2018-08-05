package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2018/7/26 14:49
 */

@Entity
@SchemaDocumentation("保险信息")
@CompileStatic
@Bostype("A09")
public class Insurance extends BosEntity{

    @SchemaDocumentation("购车单编号")
    String carNumber;

    @SchemaDocumentation("保险公司")
    String company;

    @SchemaDocumentation("保险金额")
    String amount;

    @SchemaDocumentation("保险时间")
    Long time;

    @SchemaDocumentation("客户名字")
    String customerName;

    @SchemaDocumentation("客户电话")
    String customerTel;

    @SchemaDocumentation("返点")
    BigDecimal rebate;

    @SchemaDocumentation("返点时间")
    Long rebateTime;

    @SchemaDocumentation("业务员")
    @ManyToOne
    Salesman salesman;

    @SchemaDocumentation("合作单位")
    String cooperativeUnit;

    @SchemaDocumentation("业务员提成")
    String salesmanExtract;

    @SchemaDocumentation("提车人")
    String carryCarPeople;

    @SchemaDocumentation("备注")
    String remark;

    @SchemaDocumentation("图片")
    String images;
}
