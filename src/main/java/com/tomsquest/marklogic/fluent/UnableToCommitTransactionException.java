package com.tomsquest.marklogic.fluent;

import org.apache.http.StatusLine;

public class UnableToCommitTransactionException extends FluentClientException {
    public UnableToCommitTransactionException(Transaction transaction, StatusLine statusLine) {
        super("Unable to commit " + transaction + ". Error: " + statusLine);
    }
}
