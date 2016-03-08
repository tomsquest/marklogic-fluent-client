package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class TextWriter implements Writer {

    private static final Logger LOG = LoggerFactory.getLogger(TextWriter.class);
    private final Client client;

    public TextWriter(Client client) {
        this.client = client;
    }

    @Override
    public void write(Client.WriteOperation writeOperation) {
        String uri = buildUri(writeOperation);
        Request request = buildRequest(writeOperation, uri);
        HttpResponse response = execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            LOG.info("Success writing to {}", uri);
        } else {
            throw new UnableToWriteException(uri, response);
        }
    }

    private String buildUri(Client.WriteOperation writeOperation) {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.getServerUrl() + "/v1/documents");
            uriBuilder.addParameter("uri", writeOperation.getUri());

            if (writeOperation.getCollections() != null) {
                writeOperation.getCollections().forEach(coll -> uriBuilder.addParameter("collection", coll));
            }

            if (writeOperation.getTransaction() != null) {
                uriBuilder.addParameter("txid", writeOperation.getTransaction().getId());
            }

            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new FluentClientException(e);
        }
    }

    private Request buildRequest(Client.WriteOperation writeOperation, String uri) {
        Request request = Request
                .Put(uri)
                .bodyString(
                        writeOperation.getValue().toString(),
                        ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

        if (writeOperation.getTransaction() != null) {
            request.addHeader("Cookie", "HostId=" + writeOperation.getTransaction().getHostId());
        }

        return request;
    }

    private HttpResponse execute(Request request) {
        try {
            return Executor
                    .newInstance(client.getHttpClient())
                    .execute(request)
                    .returnResponse();
        } catch (Exception e) {
            throw new UnableToWriteException(request, e);
        }
    }
}
