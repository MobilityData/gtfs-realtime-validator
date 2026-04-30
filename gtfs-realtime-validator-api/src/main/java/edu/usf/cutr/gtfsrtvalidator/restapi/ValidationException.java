package edu.usf.cutr.gtfsrtvalidator.restapi;

import io.javalin.http.HttpStatus;

import java.util.Objects;

public class ValidationException extends Exception {

    private final HttpStatus status;

    public ValidationException(HttpStatus status, String message) {
        super(message);
        this.status = Objects.requireNonNull(status);
    }

    public ValidationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = Objects.requireNonNull(status);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
