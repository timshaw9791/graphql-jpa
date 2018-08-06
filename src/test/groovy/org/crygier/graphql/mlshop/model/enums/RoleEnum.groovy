package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

@SchemaDocumentation("类型,当前类型分为自营/合作，对应回传信息为own/coporate；")
enum RoleEnum implements BosEnum {
    SUPERADMIN("SUPERADMIN", "超级管理员"),
    ADMIN("ADMIN", "业务经理" );

    private RoleEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class RoleEnumConverter extends BosEnumConverter<RoleEnum> {}
}