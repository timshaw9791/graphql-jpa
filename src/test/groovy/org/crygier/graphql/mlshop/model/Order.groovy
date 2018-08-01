package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.OrderAllocateStatusEnum
import org.crygier.graphql.mlshop.model.enums.OrderStatusEnum
import org.crygier.graphql.mlshop.model.enums.OrderTypeEnum

import javax.persistence.*

/**
 * @author Curtain
 * @date 2018/7/30 9:55
 */

@Entity
@SchemaDocumentation("订单")
@CompileStatic
@Bostype("A10")
@Table(name = "T_order")
public class Order extends BosEntity {
    @SchemaDocumentation("订单状态")
    OrderStatusEnum orderStatusEnum;

    @SchemaDocumentation("订单分配")
    OrderAllocateStatusEnum orderAllocateStatusEnum;

    @SchemaDocumentation("订单类别")
    OrderTypeEnum orderTypeEnum;

    @SchemaDocumentation("门店")
    @ManyToOne(fetch = FetchType.LAZY)
    Shop shop;

    @SchemaDocumentation("经手人")
    String operator;

    @SchemaDocumentation("客户信息")
    @ManyToOne(fetch = FetchType.LAZY)
    Customer customer;

    @SchemaDocumentation("车辆信息")
    @ManyToOne(fetch = FetchType.LAZY)
    CarInfo carInfo;

    @SchemaDocumentation("方案")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<PayScheme> paySchemes=new HashSet<>();

    @SchemaDocumentation("保险订单集合")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<InsuranceItems> insuranceItems=new HashSet<>();

    @SchemaDocumentation("按揭")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<Mortgage> mortgages=new HashSet<>();

    @SchemaDocumentation("装潢")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    Set<Decor> decors=new HashSet<>();

    @SchemaDocumentation("合格证复印件")
    String certificateImage;

    @SchemaDocumentation("保险原件")
    String originalInsurance;
}
