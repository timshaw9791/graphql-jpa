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

//    @SchemaDocumentation("首富")
//
//    @SchemaDocumentation("月供")
//
//    @SchemaDocumentation("期数")
//    @SchemaDocumentation("定金")
//    @SchemaDocumentation("赠送")
//    @SchemaDocumentation("服务费")
//    @SchemaDocumentation("个人名下/公司名下")


}
