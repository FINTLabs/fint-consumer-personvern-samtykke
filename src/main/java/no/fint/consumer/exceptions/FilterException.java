package no.fint.consumer.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FilterException extends RuntimeException {
    private final HttpStatus httpStatus;

    public FilterException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public FilterException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}
