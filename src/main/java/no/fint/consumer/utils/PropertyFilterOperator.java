package no.fint.consumer.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum PropertyFilterOperator {
    EQUALS("eq");

    private final String value;

    private static final Map<String, PropertyFilterOperator> BY_VALUE = new HashMap<>();

    static {
        Arrays.stream(PropertyFilterOperator.values())
                .forEach(operator -> BY_VALUE.put(operator.value, operator));
    }

    PropertyFilterOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PropertyFilterOperator valueOfOperator(String value) {
        return BY_VALUE.get(value);
    }
}
