package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity

/**
 * @author Curtain
 * @date 2018/8/14 9:30
 */

@Entity
@SchemaDocumentation("问答帮助中心")
@CompileStatic
@Bostype("A22")
class CarBrandIcon extends BosEntity {

    @SchemaDocumentation("品牌名称")
    String brand;

    @SchemaDocumentation("图标")
    String icon;

    @SchemaDocumentation("是否已选")
    boolean choose = false;
}
