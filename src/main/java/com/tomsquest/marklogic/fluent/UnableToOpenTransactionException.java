package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;

public class UnableToOpenTransactionException extends FluentClientException {

    private UnableToOpenTransactionException(String msg) {
        super(msg);
    }

    public static UnableToOpenTransactionException invalidServerResponse(HttpResponse response) {
        return new UnableToOpenTransactionException("Server returned an error: " + response);
    }

    public static UnableToOpenTransactionException noTransactionId(HttpResponse response) {
        return new UnableToOpenTransactionException("No transactionId in response's Location header. Response: " + response);
    }

    public static UnableToOpenTransactionException noHostId(HttpResponse response) {
        return new UnableToOpenTransactionException("No cookie named 'HostId' in response. Response: " + response);
    }
}
