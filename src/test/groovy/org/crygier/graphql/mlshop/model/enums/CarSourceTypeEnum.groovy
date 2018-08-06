package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("类型,当前类型分为自营/合作，对应回传信息为own/coporate")
enum CarSourceTypeEnum implements BosEnum {
    OWN("OWN", "自营"), COPORATE("COPORATE", "合作");

    private CarSourceTypeEnum(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CarSourceTypeEnumConverter extends BosEnumConverter<CarSourceTypeEnum> {}
}