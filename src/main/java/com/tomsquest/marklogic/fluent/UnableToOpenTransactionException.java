package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class UnableToOpenTransactionException extends FluentClientException {

    public UnableToOpenTransactionException(String msg) {
        super(msg);
    }

    public static UnableToOpenTransactionException invalidServerResponse(StatusLine statusLine) {
        return new UnableToOpenTransactionException("Server returned an error: " + statusLine);
    }

    public static UnableToOpenTransactionException noTransactionId(HttpResponse response) {
        return new UnableToOpenTransactionException("No transactionId in response's Location header. Response: " + response);
    }

    public static UnableToOpenTransactionException noHostId(HttpResponse response) {
        return new UnableToOpenTransactionException("No cookie named 'HostId' in response. Response: " + response);
    }
}
