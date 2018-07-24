package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Converter;

@SchemaDocumentation("测试是否为空")
public enum FieldNullEnum implements BosEnum {

    ISNULL( "ISNULL", "为空","为null"), NOTNULL("NOTNULL","不为空", "不为null");

    private FieldNullEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class FieldNullEnumConverter extends BosEnumConverter<FieldNullEnum> {}
}


