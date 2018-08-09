package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEnum;

enum OrderByDirection implements BosEnum {


    ASC("ASC", "升序"), DESC("DESC", "降序");

    public static final String ORDER_BY = "OrderBy";



    private OrderByDirection(String value, String name) {
        this.ev = new EnumInnerValue(value, name);
    }

    private EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }

}
