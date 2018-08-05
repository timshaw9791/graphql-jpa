package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum

import javax.persistence.Entity

@Entity
@SchemaDocumentation("客户信息")
@CompileStatic
@Bostype("A07")
public class Customer extends BosEntity {
    @SchemaDocumentation("客户姓名")
    String name;
    @SchemaDocumentation("联系方式")
    String tel;
    @SchemaDocumentation("身份证地址")
    String address;

    @SchemaDocumentation("现住地址")
    String addressNow;

    @SchemaDocumentation("身份证号")
    String idcard;

    @SchemaDocumentation("是否审核  默认false")
    Boolean audit = false;

    @SchemaDocumentation("身份证图片，可多张上传图片，请以分号隔开图片短url")
    String idcardpicurls;

    @SchemaDocumentation("客户级别：分为ABC三等")
    CustomerLevelEnum level;

    @SchemaDocumentation("微信号")
    String weixinid;

    @SchemaDocumentation("客户微信昵称")
    String weixinnick;

    @SchemaDocumentation("出生年月")
    Long birthDate;

    @SchemaDocumentation("性别")
    String gender;



}
