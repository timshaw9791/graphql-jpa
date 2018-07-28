package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * @author Curtain
 * @date 2018/7/27 19:41
 */

@Entity
@SchemaDocumentation("车辆信息")
@CompileStatic
@Bostype("A11")
class CarInfo extends BosEntity {

    @SchemaDocumentation("汽车配置信息")
    @ManyToOne(fetch = FetchType.LAZY)
    CarConfigInfo carConfigInfo;

    @SchemaDocumentation("门店")
    @ManyToOne(fetch = FetchType.LAZY)
    Shop shop;

    @SchemaDocumentation("销售数量")
    String  salesVolume;

    @SchemaDocumentation("图片")
    String image;

    @SchemaDocumentation("标签")
    String label;

    @SchemaDocumentation("配置亮点")
    String brightPoints;

    @SchemaDocumentation("车型亮点")
    String carBrightPoints;

    @SchemaDocumentation("金融方案")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<FinancialScheme> financialSchemesItems = new HashSet<>();

}
