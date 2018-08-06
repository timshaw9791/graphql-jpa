package cn.wzvtcsoft.x.bos.domain;

public interface BosEnum {
    public static class EnumInnerValue {
        private String value;
        private String alias;
        private String description;

        public EnumInnerValue(String value, String alias) {
            this.value = value;
            this.alias = alias;
            this.description = "";
        }

        private String getValue() {
            return value;
        }

        public String getName() {
            return alias;
        }

        public String getDescription() {
            return description;
        }
    }

    public BosEnum.EnumInnerValue getEnumInnerValue();

    default public String getValue() {
        return getEnumInnerValue().getValue();
    }

    default public String getAlias() {
        return getEnumInnerValue().getName();
    }

    default public String getDescription() {
        return getEnumInnerValue().getDescription();
    }
}
