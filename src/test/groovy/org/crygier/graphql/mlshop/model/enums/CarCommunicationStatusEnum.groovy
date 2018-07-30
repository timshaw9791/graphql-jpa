package org.crygier.graphql.mlshop.model.enums;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue;
import org.crygier.graphql.BosEnumConverter;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Converter;

/**
 * @author Curtain
 * @date 2018/7/26 9:13
 */
@SchemaDocumentation("买车沟通状态分为五种、A 待分配, B 待回访，C 已回访，D 已转换，E 战败（F 已结束）五种   买车沟通用的是战败   保险回访用的是已结束")
public enum CarCommunicationStatusEnum implements BosEnum {
    A("A", "待分配"),
    B("B", "待回访"),
    C("C", "已回访"),
    D("D", "已转换"),
    E("E", "战败"),
    F("F", "已结束");

    private CarCommunicationStatusEnum(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CarCommunicationStatusEnumConverter extends BosEnumConverter<CarCommunicationStatusEnum> {
    }
}
