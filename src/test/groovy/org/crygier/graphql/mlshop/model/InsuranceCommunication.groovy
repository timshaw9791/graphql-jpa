package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * @author Curtain
 * @date 2018/7/27 10:50
 */

@Entity
@SchemaDocumentation("保险回访")
@CompileStatic
@Bostype("A10")
class InsuranceCommunication extends BosEntity {

    @SchemaDocumentation("状态：分为待分配,待回访，已回访，已转换，已结束五种")
    CarCommunicationStatusEnum status=CarCommunicationStatusEnum.A;

    @SchemaDocumentation("客户姓名")
    String customerName;

    @SchemaDocumentation("联系方式")
    String tel;

    @SchemaDocumentation("现住地址")
    String addressNow;

    @SchemaDocumentation("微信号")
    String weixinid;

    @SchemaDocumentation("业务员")
    @ManyToOne
    Salesman salesman;

    @SchemaDocumentation("分配人")
    @ManyToOne
    Administ administ;

    @SchemaDocumentation("分配时间")
    Long distributeTime;

    @SchemaDocumentation("客户微信昵称")
    String weixinnick;

    @SchemaDocumentation("保险信息")
    @ManyToOne
    Insurance insurance;

    @SchemaDocumentation("分配人")
    @ManyToOne
    Administ administ;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<CommunicationRecord> communicationItems = new HashSet<>();


    public void distribute(Salesman salesman) {
        this.setSalesman(salesman);
        this.setDistributeTime(System.currentTimeMillis());
        this.setStatus(CarCommunicationStatusEnum.B);
    }

}
