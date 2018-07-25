package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("门店")
@CompileStatic
@Bostype("A05")
public class Shop extends BosEntity {
    @SchemaDocumentation("名称")
    String name;
    @SchemaDocumentation("联系方式")
    String tel;
    @SchemaDocumentation("地址")
    String address;

    @SchemaDocumentation("类型，分为加盟店和自营店来个闹钟")
    ShopTypeEnum shopTypeEnum;



}
