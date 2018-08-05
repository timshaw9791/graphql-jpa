package org.crygier.graphql.mlshop.model;

import org.crygier.graphql.annotation.SchemaDocumentation;


import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/8/1 16:31
 */

@Entity
@SchemaDocumentation("反馈")
@CompileStatic
@Bostype("A18")
public class Feedback extends BosEntity{

    @SchemaDocumentation("问题分类")
    String problemCategory;

    @SchemaDocumentation("问题描述")
    String problemDescription;

    @SchemaDocumentation("联系方式")
    String contact;

    @SchemaDocumentation("“客户")
    @ManyToOne
    Customer customer;
}
