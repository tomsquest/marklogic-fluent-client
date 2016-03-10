package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DeleteCommand {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteCommand.class);
    private final Client client;
    private final List<String> docUris;

    public DeleteCommand(Client client, String... docUris) {
        this.client = client;
        this.docUris = Arrays.asList(docUris);
    }

    public void run() {
        String uri = buildUri(docUris);

        HttpResponse response = sendRequest(uri);

        int receivedStatus = response.getStatusLine().getStatusCode();
        if (receivedStatus == HttpStatus.SC_NO_CONTENT) {
            LOG.info("Deleted uris {}", docUris);
        } else {
            throw new UnableToDeleteDocumentsException(docUris, response);
        }
    }

    private String buildUri(List<String> docUris) {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.getServerUrl() + "/v1/documents");
            docUris.forEach(uri -> uriBuilder.addParameter("uri", uri));
            return uriBuilder.build().toString();
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }

    private HttpResponse sendRequest(String uri) {
        try {
            return Executor.newInstance(client.getHttpClient())
                    .execute(Request.Delete(uri))
                    .returnResponse();
        } catch (IOException e) {
            throw new UnableToDeleteDocumentsException(docUris, e);
        }
    }
}
