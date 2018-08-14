package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("类型，分为加盟店和自营店两种，OWN为自营，COPORATE为加盟")
enum ShopTypeEnum implements BosEnum {
    OWN("OWN", "自营" ), COPORATE("COPORATE","加盟");

    private ShopTypeEnum(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class ShopTypeEnumConverter extends BosEnumConverter<ShopTypeEnum> {}
}
