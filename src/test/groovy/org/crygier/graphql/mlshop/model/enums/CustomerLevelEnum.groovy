package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("客户登记，被分为A/B/C,A为最高级别，B为中等级别，C为最低级别")
enum CustomerLevelEnum implements BosEnum {
    A("A", "A级"),
    B("B", "B级"),
    C("C", "C级");

    private CustomerLevelEnum(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CustomerLevelEnumConverter extends BosEnumConverter<CustomerLevelEnum> {}
}