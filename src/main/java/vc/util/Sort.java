package vc.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jooq.SortOrder;

public enum Sort {
    ASC, DESC;

    @JsonCreator
    public static Sort fromValue(String value) {
        return Sort.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    public SortOrder toJooq() {
        return this == ASC ? SortOrder.ASC : SortOrder.DESC;
    }
}
