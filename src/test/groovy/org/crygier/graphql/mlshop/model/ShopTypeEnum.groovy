package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("类型，分为加盟店和自营店两种")
enum ShopTypeEnum implements BosEnum {
    OWN("OWN", "自营", "加盟"), COPORATE("COPORATE", "加盟", "与人合作的");

    private ShopTypeEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class ShopTypeEnumConverter extends BosEnumConverter<ShopTypeEnum> {}
}
