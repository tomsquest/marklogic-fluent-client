package com.tomsquest.marklogic.fluent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import java.nio.charset.StandardCharsets;

public class TransactionOpener {
    private final Config config;

    public TransactionOpener(Config config) {
        this.config = config;
    }

    public Transaction openTransaction(Client client) {
        try {
            URIBuilder uriBuilder = new URIBuilder(config.getUrl() + "/LATEST/transactions");

            Request request = Request
                    .Post(uriBuilder.build())
                    .bodyString("", ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

            HttpClient httpClient = HttpClients.custom().disableRedirectHandling().build();
            HttpResponse response = Executor
                    .newInstance(httpClient)
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            config.getUser(),
                            config.getPass())
                    )
                    .execute(request)
                    .returnResponse();

            String transactionId = extractTransactionId(response);
            String hostId = extractHostId(response);

            return new Transaction(client, transactionId, hostId);
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

        throw new RuntimeException("Unable to get transactionId from response. Response: "+ response);
    }

    private String extractHostId(HttpResponse response) {
        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if( cookieHeader != null) {
            String value = cookieHeader.getValue();
            if (value.contains("HostId")) {
                return value.substring(value.lastIndexOf("=") + 1);
            }
        }

        throw new RuntimeException("Unable to get hostId from response. Response: "+ response);
    }
}
