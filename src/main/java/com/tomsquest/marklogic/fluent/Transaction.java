package com.tomsquest.marklogic.fluent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction {

    private static final Logger LOG = LoggerFactory.getLogger(Transaction.class);
    private final Client client;

    private String id;
    private String hostId;

    public Transaction(Client client) {
        this.client = client;
    }

    /**
     * VisibleForTesting
     */
    static Transaction openedTransaction(Client client, String transactionId, String hostId) {
        Transaction tran = new Transaction(client);
        tran.id = transactionId;
        tran.hostId = hostId;
        return tran;
    }

    public Transaction open() {
        HttpResponse response = executePost(client.getServerUrl() + "/v1/transactions/");

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER) {
            this.id = extractTransactionId(response);
            this.hostId = extractHostId(response);
            LOG.info("{} opened", this);
            return this;
        } else {
            throw UnableToOpenTransactionException.invalidServerResponse(response.getStatusLine());
        }
    }

    public void commit() {
        HttpResponse response = executePost(client.getServerUrl() + "/v1/transactions/" + id + "?result=commit");

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            LOG.info("{} commited", this);
        } else {
            throw new UnableToCommitTransactionException(this, response.getStatusLine());
        }
    }

    public void rollback() {
        HttpResponse response = executePost(client.getServerUrl() + "/v1/transactions/" + id + "?result=rollback");

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            LOG.info("{} rollbacked", this);
        } else {
            throw new UnableToRollbackTransactionException(this, response.getStatusLine());
        }
    }

    public String getId() {
        return id;
    }

    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return "Transaction{id='" + id + '\'' + ", hostId='" + hostId + '\'' + '}';
    }

    private String extractTransactionId(HttpResponse response) {
        Header locationHeader = response.getFirstHeader("Location");
        if (locationHeader != null) {
            String value = locationHeader.getValue();
            if (value.contains("/transactions/")) {
                return value.substring(value.lastIndexOf("/") + 1);
            }
        }

        throw UnableToOpenTransactionException.noTransactionId(response);
    }

    private String extractHostId(HttpResponse response) {
        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if (cookieHeader != null) {
            String value = cookieHeader.getValue();
            if (value.contains("HostId")) {
                return value.substring(value.lastIndexOf("=") + 1);
            }
        }

        throw UnableToOpenTransactionException.noHostId(response);
    }

    private HttpResponse executePost(String uri) {
        try {
            return Executor
                    .newInstance(client.getHttpClient())
                    .execute(Request
                            .Post(uri)
                            .addHeader("Cookie", "HostId=" + hostId))
                    .returnResponse();
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }
}
