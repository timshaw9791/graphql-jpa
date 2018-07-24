package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public abstract class BosEnumConverter<T extends BosEnum>  implements AttributeConverter<T,String> {
    private Class<T> type;
    public BosEnumConverter(){
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }



    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute.getValue();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            return Arrays.stream(((Class<BosEnum>) type).getEnumConstants()).filter(
                    enumValue -> ((T) enumValue).getValue().equals(dbData))
                    .map(enumValue -> ((T) enumValue))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("enum convertError!" + dbData + ":" + type.getClass().getCanonicalName()));
        }
    }
}


