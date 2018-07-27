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

    @SchemaDocumentation("用品牌作为key")
    String brand;

    @SchemaDocumentation("车辆配置信息 每个字段以json格式 key value表示")
    String info;


}
