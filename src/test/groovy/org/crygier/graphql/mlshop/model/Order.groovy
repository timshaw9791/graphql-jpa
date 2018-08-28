package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.OrderAllocateStatusEnum
import org.crygier.graphql.mlshop.model.enums.OrderPayStatusEnum
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

    @SchemaDocumentation("支付状态")
    OrderPayStatusEnum payStatusEnum;

    @SchemaDocumentation("门店")
    @ManyToOne
    Shop shop;

    @SchemaDocumentation("经手人(门店中的业务员)")
    @ManyToOne
    Salesman salesman;

    @SchemaDocumentation("备注")
    String remark;

    @SchemaDocumentation("客户信息")
    @ManyToOne
    Customer customer;

    @SchemaDocumentation("保险")
    @ManyToOne
    Insurance insurance;

    @SchemaDocumentation("品牌")
    String brand;

    @SchemaDocumentation("型号")
    String model;

    @SchemaDocumentation("车身颜色")
    String carColor;

    @SchemaDocumentation("数量")
    Integer count;

    @SchemaDocumentation("详细数据")
    String carDescribe;

    @SchemaDocumentation("定金：/分")
    Long frontMoney;

    @SchemaDocumentation("尾款：/分")
    Long tailMoney;

    @SchemaDocumentation("车辆来源")
    @ManyToOne
    CarSource carSource;

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

    @SchemaDocumentation("登记证书")
    String registrationCertificate;

    @SchemaDocumentation("纸质表格")
    String paperTable;

    @SchemaDocumentation("车辆信息")
    @ManyToOne
    CarInfo carInfo;
    //todo  carindo 应该用 vehicleprice 代替

    @SchemaDocumentation("砍价是否成功")
    boolean bargainSuccess = false;
}
