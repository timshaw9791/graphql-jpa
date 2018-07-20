package org.crygier.graphql.mlshop.model;


import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;

@Entity
@SchemaDocumentation("车辆来源")
@CompileStatic
@Bostype("A03")
public class CarSource extends BosEntity {
    @SchemaDocumentation("名称")
    String name;
    @SchemaDocumentation("联系方式")
    String tel;
    @SchemaDocumentation("地址")
    String address;
    //TODO 后续考虑enum
    @SchemaDocumentation("类型,当前类型分为自营/合作，对应回传信息为own/coporate,是否禁用")
    String type;
    //TODO 后续考虑enum
    @SchemaDocumentation("是否禁用")
    boolean disabled=false;
}
