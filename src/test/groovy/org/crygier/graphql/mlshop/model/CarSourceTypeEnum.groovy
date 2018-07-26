package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.FieldNullEnum
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("类型,当前类型分为自营/合作，对应回传信息为own/coporate")
enum CarSourceTypeEnum implements BosEnum {
    OWN("OWN", "自营", "自己经营的"), COPORATE("COPORATE", "合作", "与人合作的");

    private CarSourceTypeEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CarSourceTypeEnumConverter extends BosEnumConverter<CarSourceTypeEnum> {}
}