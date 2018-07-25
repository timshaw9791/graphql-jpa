package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("管理员")
@CompileStatic
@Bostype("A06")
public class Administ extends BosEntity {
    @SchemaDocumentation("姓名")
    String name;
    @SchemaDocumentation("联系方式")
    String tel;

    @SchemaDocumentation("性别")
    String pwd;

    @SchemaDocumentation("出生年月")
    String birthday

    @SchemaDocumentation("客户等级")
    CustomerLevelEnum level;


}
