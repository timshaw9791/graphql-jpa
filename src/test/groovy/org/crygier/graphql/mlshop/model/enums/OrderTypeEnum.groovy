package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

/**
 * @author Curtain
 * @date 2018/7/30 14:58
 */
@SchemaDocumentation("订单状态 分为三种A:全额购车单  /  B:厂价金融购车单  /  C:按揭购车单")
enum OrderTypeEnum implements BosEnum{
    A("A", "全额购车单"),
    B("B", "厂价金融购车单"),
    C("C", "按揭购车单");

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class OrderTypeEnumConverter extends BosEnumConverter<OrderTypeEnum> {}
}