package no.fint.consumer.exceptions;

public class FilterException extends RuntimeException {

    public FilterException(String message) {
        super(message);
    }

    public FilterException(Throwable cause) {
        super(cause);
    }
}
