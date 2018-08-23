package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

/**
 * @author Curtain
 * @date 2018/7/30 10:24
 */

@SchemaDocumentation("订单状态 分为四种NEW:新订单 /  PENDING 待处理    /  FINISH:已完结  /  REFUND:已退款")
enum OrderStatusEnum implements BosEnum{
    NEW("NEW", "新订单"),
    PENDING("PENDING", "待处理"),
    FINISH("FINISH", "已完结"),
    REFUND("REFUND", "已退款");

    private OrderStatusEnum(String value, String name) {
        this.ev = new BosEnum.EnumInnerValue(value, name);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }


    @Converter(autoApply = true)
    public static class OrderStatusEnumConverter extends BosEnumConverter<OrderStatusEnum> {}
}