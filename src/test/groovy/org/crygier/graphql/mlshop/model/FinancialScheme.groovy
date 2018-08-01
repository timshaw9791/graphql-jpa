package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.Bostype;
import cn.wzvtcsoft.x.bos.domain.Entry;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/7/27 20:01
 */

@Entity
@SchemaDocumentation("金融方案")
@CompileStatic
@Bostype("A12")
public class FinancialScheme extends Entry {

    @SchemaDocumentation("首付:单位分")
    Long downPayment;

    @SchemaDocumentation("月供:单位分")
    Long monthly;

    @SchemaDocumentation("首付百分比")
    BigDecimal downPaymentRate;

    @SchemaDocumentation("期数/月")
    Long periods;

    @SchemaDocumentation("定金:单位分")
    Long deposit;

    @SchemaDocumentation("赠送:单位分")
    Long presenter;

    @SchemaDocumentation("服务费:单位分")
    Long serveMoney;

    @SchemaDocumentation("个人名下/公司名下")
    String underName;

    @SchemaDocumentation("方案更新时间")
    Long schemeTime;


}
