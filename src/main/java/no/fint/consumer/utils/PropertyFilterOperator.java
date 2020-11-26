package no.fint.consumer.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum PropertyFilterOperator {
    EQUALS("="),
    CONTAINS("~");

    private final String delimiter;

    private static final Map<String, PropertyFilterOperator> BY_DELIMITER = new HashMap<>();

    static {
        Arrays.stream(PropertyFilterOperator.values())
                .forEach(operator -> BY_DELIMITER.put(operator.delimiter, operator));
    }

    PropertyFilterOperator(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public static PropertyFilterOperator valueOfOperator(String delimiter) {
        return BY_DELIMITER.get(delimiter);
    }
}
