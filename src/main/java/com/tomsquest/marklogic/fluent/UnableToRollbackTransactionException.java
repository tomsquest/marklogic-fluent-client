package com.tomsquest.marklogic.fluent;

import org.apache.http.StatusLine;

public class UnableToRollbackTransactionException extends FluentClientException {
    public UnableToRollbackTransactionException(Transaction transaction, StatusLine statusLine) {
        super("Unable to rollback " + transaction + ". Error: " + statusLine);
    }
}
