package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;

import java.util.List;

public class UnableToDeleteDocumentsException extends FluentClientException {
    public UnableToDeleteDocumentsException(List<String> docUris, Exception e) {
        super("Unable to delete docs '" + docUris + "'. Error: " + e.getMessage(), e);
    }

    public UnableToDeleteDocumentsException(List<String> docUris, HttpResponse response) {
        super("Unable to delete docs '" + docUris + "'. Server response: " + response);
    }
}
