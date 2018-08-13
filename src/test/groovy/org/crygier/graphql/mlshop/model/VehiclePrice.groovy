package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity;
import javax.persistence.ManyToOne
import javax.persistence.OneToMany;

/**
 * @author Curtain
 * @date 2018/8/13 15:11
 */
@Entity
@SchemaDocumentation("车辆价格")
@CompileStatic
@Bostype("A19")
public class VehiclePrice extends BosEntity{
    @ManyToOne
    @SchemaDocumentation("门店")
    Shop shop;

    @ManyToOne
    @SchemaDocumentation("车辆信息")
    CarInfo carInfo;

    @SchemaDocumentation("金融方案")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<FinancialScheme> financialSchemesItems = new HashSet<>();

    @SchemaDocumentation("方案扫描件（核算方案）")
    String schemeScanImage;
}
