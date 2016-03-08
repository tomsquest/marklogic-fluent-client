package com.tomsquest.marklogic.fluent;

public class FluentClientException extends RuntimeException {

    public FluentClientException(Throwable cause) {
        super(cause);
    }

    public FluentClientException(String message) {
        super(message);
    }

    public FluentClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
