package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity

/**
 * @author Curtain
 * @date 2018/7/27 19:08
 */

@Entity
@SchemaDocumentation("车辆配置信息")
@CompileStatic
@Bostype("A10")
class CarConfigInfo extends BosEntity {

    @SchemaDocumentation("品牌")
    String brand;

    @SchemaDocumentation("型号")
    String model;

    @SchemaDocumentation("厂商指导价")
    String guidePrice;

    @SchemaDocumentation("阿里云服务器文件名")
    String filename;

}
