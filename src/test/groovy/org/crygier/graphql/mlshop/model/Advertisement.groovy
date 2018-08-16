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
@SchemaDocumentation("广告")
@CompileStatic
@Bostype("A18")
public class Advertisement extends BosEntity {

    @SchemaDocumentation("广告模板")
    String template;

    @SchemaDocumentation("文本内容")
    String textContent;

    @SchemaDocumentation("轮播时间 单位：秒")
    Long time;

    @SchemaDocumentation("图片")
    String image;

    @SchemaDocumentation("广告网址")
    String url;

    @SchemaDocumentation("文章标题")
    String articleTitle;

    @SchemaDocumentation("品牌名")
    String brand;

    @SchemaDocumentation("图标")
    String icon;

    @SchemaDocumentation("子标题")
    String subtitle;

    @SchemaDocumentation("选区")
    String constituency;


}