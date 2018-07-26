package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.FieldNullEnum
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("客户登记，被分为A/B/C")
enum CustomerLevelEnum implements BosEnum {
    A("A", "A级", "最高级别"),
    B("B", "B级", "中等级别"),
    C( "C", "C级", "最低级别" );

    private CustomerLevelEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CustomerLevelEnumConverter extends BosEnumConverter<CustomerLevelEnum> {}
}