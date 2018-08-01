package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum
import org.crygier.graphql.mlshop.model.enums.CarCommunicationTypeEnum;

import javax.persistence.*

/**
 * @author Curtain
 * @date 2018/7/26 8:49
 */

@Entity
@SchemaDocumentation("买车沟通")
@CompileStatic
@Bostype("A08")
public class CarCommunication extends BosEntity {

    @SchemaDocumentation("关注车型")
    String carType;

    @SchemaDocumentation("状态：分为待分配,待回访，已回访，已转换，战败五种")
    CarCommunicationStatusEnum status;

    @SchemaDocumentation("类型：分为来访，回访两种")
    CarCommunicationTypeEnum type;

    @SchemaDocumentation("客户信息")
    @ManyToOne
    Customer customer;

    @SchemaDocumentation("详细沟通记录")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<CommunicationRecord> communicationItems = new HashSet<>();

    @SchemaDocumentation("认证手机号")
    String phone;
}
