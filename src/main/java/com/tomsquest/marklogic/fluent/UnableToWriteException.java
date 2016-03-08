package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

public class UnableToWriteException extends FluentClientException {

    public UnableToWriteException(String uri, HttpResponse response) {
        super("Unable to write to uri '" + uri + "'. Response: " + response);
    }

    public UnableToWriteException(Request request, Exception e) {
        super("Unable to write to uri '" + request + "'", e);
    }
}
