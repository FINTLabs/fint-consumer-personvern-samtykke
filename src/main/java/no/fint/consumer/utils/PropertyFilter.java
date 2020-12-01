package no.fint.consumer.utils;

import no.fint.consumer.exceptions.FilterException;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.Link;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PropertyFilter {
    private static final Set<String> values = Arrays.stream(PropertyFilterOperator.values())
            .map(PropertyFilterOperator::getValue)
            .collect(Collectors.toSet());

    private final static Pattern PATTERN = Pattern.compile("(?<collection>(?<path>[\\w/]+?)/(?<lambda>any)\\(\\w+:\\w+/)?" +
            "(?<property>[\\w/]+?) (?<operator>" + String.join("|", values) + ") '(?<value>[\\S ]+?)'\\)?");

    private PropertyFilter() {
    }

    public static <T extends FintLinks> Stream<T> from(Stream<T> resources, String filter) {
        Matcher matcher = PATTERN.matcher(filter);

        if (matcher.matches()) {
            String collection = matcher.group("collection");
            String path = matcher.group("path");
            String lambda = matcher.group("lambda");
            String property = matcher.group("property").replaceAll("/", ".");
            PropertyFilterOperator operator = PropertyFilterOperator.valueOfOperator(matcher.group("operator"));
            String value = matcher.group("value").replaceAll("'", "");

            if (collection == null) {
                return resources.filter(resource -> filter(resource, property, operator, value));
            }

            return resources.filter(resource -> filter(resource, path, lambda, property, operator, value));
        }

        throw new FilterException(HttpStatus.BAD_REQUEST, "Invalid or unsupported syntax");
    }

    public static <T extends FintLinks> boolean filter(T resource, String property, PropertyFilterOperator operator, String value) {
        try {
            Object object = PropertyUtils.getProperty(resource, property);

            return comparator(object, operator, value);

        } catch (Exception e) {
            throw new FilterException(HttpStatus.BAD_REQUEST, e);
        }
    }

    public static <T extends FintLinks> boolean filter(T resource, String path, String lambda, String property, PropertyFilterOperator operator, String value) {
        try {
            Object collection = PropertyUtils.getProperty(resource, path.replaceAll("/", "."));

            if (collection instanceof List) {
                List<Object> objects = new ArrayList<>((List<?>) collection);

                for (Object item : objects) {
                    Object object = PropertyUtils.getProperty(item, property);

                    if (item instanceof Link) {
                        object = StringUtils.substringAfterLast(String.valueOf(object), "}");
                    }

                    if (comparator(object, operator, value)) {
                        return true;
                    }
                }

                return false;

            } else {
                throw new FilterException(HttpStatus.BAD_REQUEST, String.format("%s is not a collection", path));
            }

        } catch (Exception e) {
            throw new FilterException(HttpStatus.BAD_REQUEST, e);
        }
    }

    private static boolean comparator(Object property, PropertyFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return equals(property, value);
            default:
                throw new FilterException(HttpStatus.NOT_IMPLEMENTED, operator.toString());
        }
    }

    private static boolean equals(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).equalsIgnoreCase(value);

        } else if (property instanceof Boolean) {
            return property.equals(Boolean.parseBoolean(value));

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).equals(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS));

        } else {
            throw new FilterException(HttpStatus.NOT_IMPLEMENTED, property.getClass().getSimpleName());
        }
    }
}
