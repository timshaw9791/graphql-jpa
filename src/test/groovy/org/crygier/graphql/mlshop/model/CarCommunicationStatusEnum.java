package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.BosEnum;
import org.crygier.graphql.BosEnumConverter;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Converter;

/**
 * @author Curtain
 * @date 2018/7/26 9:13
 */
@SchemaDocumentation("买车沟通状态分为五种、A 待分配, B 待回访，C 已回访，D 已转换，E 战败五种")
public enum CarCommunicationStatusEnum implements BosEnum {
    A("A", "待分配", ""),
    B("B", "待回访", ""),
    C("C", "已回访", ""),
    D("D", "已转换", ""),
    E( "E", "战败", "" );

    private CarCommunicationStatusEnum(String value, String name, String description) {
        this.ev = new EnumInnerValue(value, name, description);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class CarCommunicationStatusEnumConverter extends BosEnumConverter<CarCommunicationStatusEnum> {}
}
