package no.fint.consumer.utils;

import no.fint.consumer.exceptions.FilterException;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.Link;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PropertyFilter {
    private static final Set<String> delimiters = Arrays.stream(PropertyFilterOperator.values())
            .map(PropertyFilterOperator::getDelimiter)
            .collect(Collectors.toSet());

    private PropertyFilter() {
    }

    public static <T extends FintLinks> Stream<T> of(Stream<T> resources, String filter) {
        String[] split = filter.split(String.join("|", delimiters));

        if (split.length == 2) {
            String property =  StringUtils.deleteWhitespace(split[0]);
            String value = split[1].trim();

            String delimiter = StringUtils.substringBetween(filter, property, value);

            PropertyFilterOperator operator = PropertyFilterOperator.valueOfOperator(delimiter);

            return resources.filter(resource -> filter(resource, property, operator, value));

        } else {
            throw new FilterException("Invalid syntax");
        }
    }

    public static <T extends FintLinks> boolean filter(T resource, String property, PropertyFilterOperator operator, String value) {
        try {
            Object object = PropertyUtils.getProperty(resource, property);

            if (object == null) {
                throw new FilterException(String.format("%s is null", property));
            }

            switch (operator) {
                case EQUALS:
                    return equals(object, value);
                case CONTAINS:
                    return contains(object, value);
                default:
                    return false;
            }

        } catch (Exception e) {
            throw new FilterException(e);
        }
    }

    private static boolean equals(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).equalsIgnoreCase(value);

        } else if (property instanceof Boolean) {
            return property.equals(Boolean.parseBoolean(value));

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).equals(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS));

        } else if (property instanceof List) {
            List<Object> objects = new ArrayList<>((List<?>) property);

            if (objects.isEmpty()) {
                return false;
            }

            Object item = objects.get(0);

            if (item instanceof Link) {
                return objects.stream()
                        .map(Link.class::cast)
                        .map(Link::getHref)
                        .map(String::toLowerCase)
                        .anyMatch(href -> href.endsWith(value.toLowerCase()));

            } else {
                throw new FilterException("Not supported yet");
            }

        } else {
            throw new FilterException("Not supported yet");
        }
    }

    private static boolean contains(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).toLowerCase().contains(value.toLowerCase());
        } else if (property instanceof List) {
            List<Object> objects = new ArrayList<>((List<?>) property);

            if (objects.isEmpty()) {
                return false;
            }

            Object item = objects.get(0);

            if (item instanceof Link) {
                return objects.stream()
                        .map(Link.class::cast)
                        .map(Link::getHref)
                        .map(String::toLowerCase)
                        .anyMatch(href -> href.contains(value.toLowerCase()));

            } else {
                throw new FilterException("Not supported yet");
            }

        } else {
            throw new FilterException("Not supported yet");
        }
    }
}
