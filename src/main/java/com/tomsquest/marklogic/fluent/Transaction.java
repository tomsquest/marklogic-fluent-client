package com.tomsquest.marklogic.fluent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import java.nio.charset.StandardCharsets;

public class Transaction {

    private final Client client;

    private String transactionId;
    private String hostId;

    public Transaction(Client client) {
        this.client = client;
    }

    public Transaction open() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.config.getUrl() + "/LATEST/transactions");

            Request request = Request
                    .Post(uriBuilder.build())
                    .bodyString("", ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

            HttpClient httpClient = HttpClients.custom().disableRedirectHandling().build();
            HttpResponse response = Executor
                    .newInstance(httpClient)
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            client.config.getUser(),
                            client.config.getPass())
                    )
                    .execute(request)
                    .returnResponse();

            this.transactionId = extractTransactionId(response);
            this.hostId = extractHostId(response);

            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.config.getUrl() + "/LATEST/transactions/" + transactionId)
                    .addParameter("result", "commit");

            HttpClient httpClient = HttpClients.custom().disableRedirectHandling().build();
            HttpResponse response = Executor
                    .newInstance(httpClient)
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            client.config.getUser(),
                            client.config.getPass())
                    )
                    .execute(Request.Post(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                // TODO log success
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            URIBuilder uriBuilder = new URIBuilder(client.config.getUrl() + "/LATEST/transactions/" + transactionId)
                    .addParameter("result", "rollback");

            HttpClient httpClient = HttpClients.custom().disableRedirectHandling().build();
            HttpResponse response = Executor
                    .newInstance(httpClient)
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            client.config.getUser(),
                            client.config.getPass())
                    )
                    .execute(Request.Post(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                // TODO log success
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String extractTransactionId(HttpResponse response) {
        Header locationHeader = response.getFirstHeader("Location");
        if (locationHeader != null) {
            String value = locationHeader.getValue();
            if (value.contains("/transactions/")) {
                return value.substring(value.lastIndexOf("/") + 1);
            }
        }

        throw new RuntimeException("Unable to get transactionId from response. Response: " + response);
    }

    private String extractHostId(HttpResponse response) {
        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if (cookieHeader != null) {
            String value = cookieHeader.getValue();
            if (value.contains("HostId")) {
                return value.substring(value.lastIndexOf("=") + 1);
            }
        }

        throw new RuntimeException("Unable to get hostId cookie from response. Response: " + response);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", hostId='" + hostId + '\'' +
                '}';
    }

    public String getTransactionId() {
        return transactionId;
    }
}
