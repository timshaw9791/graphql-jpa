package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter

/**
 * @author Curtain
 * @date 2018/7/27 10:39
 */
@SchemaDocumentation("买车接待类型，分为两种：来访、回访")
enum CarCommunicationTypeEnum implements BosEnum{

    A("A", "来访"),
    B("B", "回访");

    private CarCommunicationTypeEnum(String value, String name) {
        this.ev = new BosEnum.EnumInnerValue(value, name);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CarCommunicationTypeEnumConverter extends BosEnumConverter<CarCommunicationTypeEnum> {
    }
}
