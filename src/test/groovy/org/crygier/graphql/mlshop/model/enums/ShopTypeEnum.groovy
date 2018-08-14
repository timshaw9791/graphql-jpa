package org.crygier.graphql.mlshop.model.enums

import cn.wzvtcsoft.x.bos.domain.BosEnum
import cn.wzvtcsoft.x.bos.domain.BosEnum.EnumInnerValue
import org.crygier.graphql.BosEnumConverter
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Converter


@SchemaDocumentation("���ͣ���Ϊ���˵����Ӫ�����֣�OWNΪ��Ӫ��COPORATEΪ����")
enum ShopTypeEnum implements BosEnum {
    OWN("OWN", "��Ӫ" ), COPORATE("COPORATE","����");

    private ShopTypeEnum(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

    @Converter(autoApply = true)
    public static class ShopTypeEnumConverter extends BosEnumConverter<ShopTypeEnum> {}
}
