package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("销售员")
@CompileStatic
@Bostype("A04")
public class Salesman extends BosEntity {
    @SchemaDocumentation("姓名")
    String name;
    @SchemaDocumentation("联系方式")
    String tel;

    @SchemaDocumentation("所属门店")
    @ManyToOne
    Shop shop;

}
