package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

@SchemaDocumentation("类型,当前类型分为自营/合作，对应回传信息为own/coporate")
enum RoleEnum implements BosEnum {
    SUPERADMIN("SUPERADMIN", "超级管理员", "权限最大的账号"),
    ADMIN("ADMIN", "业务经理", "分配回访来访信息");

    private RoleEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class RoleEnumConverter extends BosEnumConverter<RoleEnum> {}
}