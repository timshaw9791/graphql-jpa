package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

/**
 * @author Curtain
 * @date 2018/7/30 14:46
 */

@SchemaDocumentation("订单分配 分为两种WAIT:未分配  /  ALREADY:已分配")
enum OrderAllocateStatusEnum implements BosEnum{
    WAIT("WAIT", "未分配"),
    ALREADY("ALREADY", "已分配");

    private OrderAllocateStatusEnum(String value, String name) {
        this.ev = new BosEnum.EnumInnerValue(value, name);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class OrderAllocateStatusEnumConverter extends BosEnumConverter<OrderAllocateStatusEnum> {}
}