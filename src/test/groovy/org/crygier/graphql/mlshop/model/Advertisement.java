package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/8/2 10:28
 */

@Entity
@SchemaDocumentation("车辆信息")
@CompileStatic
@Bostype("A18")
public class Advertisement extends BosEntity {

    @SchemaDocumentation("广告模板")
    String template;

    @SchemaDocumentation("/文本内容")
    String textContent;

    @SchemaDocumentation("轮播时间 单位：秒")
    Long time;

    @SchemaDocumentation("/image")
    String image;

    @SchemaDocumentation("广告网址")
    String url;


}
