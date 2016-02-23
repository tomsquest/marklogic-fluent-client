package com.tomsquest.marklogic.fluent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Transaction {

    private static final Logger LOG = LoggerFactory.getLogger(Transaction.class);
    private final Client client;

    private String id;
    private String hostId;

    public Transaction(Client client) {
        this.client = client;
    }

    public Transaction open() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.getServerUrl() + "/v1/transactions");

            Request request = Request
                    .Post(uriBuilder.build())
                    .bodyString("", ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

            HttpResponse response = Executor
                    .newInstance(client.getHttpClient())
                    .execute(request)
                    .returnResponse();

            this.id = extractTransactionId(response);
            this.hostId = extractHostId(response);

            LOG.info("{} opened", this);

            return this;
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }

    public void commit() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.getServerUrl() + "/v1/transactions/" + id)
                    .addParameter("result", "commit");

            HttpResponse response = Executor
                    .newInstance(client.getHttpClient())
                    .execute(Request.Post(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                LOG.info("{} commited", this);
            }
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }

    public void rollback() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.getServerUrl() + "/v1/transactions/" + id)
                    .addParameter("result", "rollback");

            HttpResponse response = Executor
                    .newInstance(client.getHttpClient())
                    .execute(Request.Post(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                LOG.info("{} rollbacked", this);
            }
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }

    public String getId() {
        return id;
    }

    private String extractTransactionId(HttpResponse response) {
        Header locationHeader = response.getFirstHeader("Location");
        if (locationHeader != null) {
            String value = locationHeader.getValue();
            if (value.contains("/transactions/")) {
                return value.substring(value.lastIndexOf("/") + 1);
            }
        }

        throw new FluentClientException("Unable to get transactionId from response. Response: " + response);
    }

    private String extractHostId(HttpResponse response) {
        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if (cookieHeader != null) {
            String value = cookieHeader.getValue();
            if (value.contains("HostId")) {
                return value.substring(value.lastIndexOf("=") + 1);
            }
        }

        throw new FluentClientException("Unable to get hostId cookie from response. Response: " + response);
    }

    @Override
    public String toString() {
        return "Transaction{id='" + id + '\'' + ", hostId='" + hostId + '\'' + '}';
    }
}
