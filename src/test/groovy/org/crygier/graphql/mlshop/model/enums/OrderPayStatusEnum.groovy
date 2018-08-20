package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter
/**
 * @author Curtain
 * @date 2018/8/20 14:13
 */
@SchemaDocumentation("订单支付状态 分为两种WAIT:未支付  /  PAID:已支付  / REFUND已退款")
enum OrderPayStatusEnum implements  BosEnum{
    WAIT("WAIT", "未支付"),
    PAID("PAID", "已支付"),
    REFUND("REFUND", "已退款");

    private OrderPayStatusEnum(String value, String name) {
        this.ev = new BosEnum.EnumInnerValue(value, name);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class OrderPayStatusEnumConverter extends BosEnumConverter<OrderPayStatusEnum> {}
}