package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
/**
 * @author Curtain
 * @date 2018/7/27 19:41
 */

@Entity
@SchemaDocumentation("车辆信息")
@CompileStatic
@Bostype("A11")
class CarInfo extends BosEntity {

    @SchemaDocumentation("车辆信息状态 是否完善")
    boolean perfectState = false;

    @SchemaDocumentation("品牌")
    String brand;

    @SchemaDocumentation("型号")
    String model;

    @SchemaDocumentation("厂商指导价")
    String guidePrice;

    @SchemaDocumentation("价格")
    BigDecimal price;

    @SchemaDocumentation("阿里云服务器文件名")
    String filename;

//    @SchemaDocumentation("门店")
//    @ManyToOne(fetch = FetchType.LAZY)
//    Shop shop;

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

//    @SchemaDocumentation("金融方案")
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
//    Set<FinancialScheme> financialSchemesItems = new HashSet<>();
//
//    @SchemaDocumentation("方案扫描件（核算方案）")
//    String schemeScan;

    @SchemaDocumentation("车身颜色")
    String color;

    @SchemaDocumentation("标签")
    String tag;

//    public void setFinancialSchemesItems(Set<FinancialScheme> financialSchemes){
//        this.financialSchemesItems =financialSchemes;
//    }

    @PrePersist
    private void prePersist(){
        price();
    }

    @PreUpdate
    private void preUpdate(){
        price();
    }

    private void price(){
        this.price = BigDecimal.valueOf(Double.valueOf(this.getGuidePrice().replace("万","")));
    }
}
