package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Client implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private final String serverUrl;
    private final CloseableHttpClient httpClient;
    private final TextWriter textWriter;

    public Client(Config config) {
        this.serverUrl = config.getAuth().getServerUrl();
        this.httpClient = config.buildHttpClient();
        this.textWriter = config.getTextWriterBuilder().build(this);
    }

    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new FluentClientException("Unable to close http client: " + httpClient, e);
            }
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public Transaction openTransaction() {
        return new Transaction(this).open();
    }

    public Client inTransaction(Consumer<Transaction> consumer) {
        Transaction tran = openTransaction();
        try {
            consumer.accept(tran);
        } catch (Exception e) {
            LOG.error("Exception thrown by consumer: " + e.getMessage(), e);
            tran.rollback();
        }
        tran.commit();
        return this;
    }

    public boolean exists(String uri) {
        try {
            URIBuilder uriBuilder = new URIBuilder(serverUrl + "/v1/documents");
            uriBuilder.addParameter("uri", uri);

            HttpResponse response = Executor
                    .newInstance(httpClient)
                    .execute(Request.Head(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                LOG.info("Document exists at uri: {}", uri);
                return true;
            }

            LOG.info("Document does not exist at uri: {}", uri);
            return false;
        } catch (Exception e) {
            throw new FluentClientException(e);
        }
    }

    public Client delete(String... uris) {
        new DeleteCommand(this, uris).run();
        return this;
    }

    public WriteToUri write(Object value) {
        return new WriteExecution(this, value);
    }

    public interface WriteToUri {
        WriteInTransaction toUri(String uri);
    }

    public interface WriteInTransaction extends WriteWithCollections {
        WriteWithCollections inTransaction(Transaction transaction);
    }

    public interface WriteWithCollections extends WriteWithTransform {
        WriteWithTransform inCollections(String... names);
    }

    public interface WriteWithTransform extends WriteWithTriples {
        WriteWithTriples transformedBy(Transform transform);
    }

    public interface WriteWithTriples extends WriteMethod {
        WriteMethod withTriples(Triples triples);
    }

    public interface WriteMethod {
        void asText();

        void asJson();

        void asXml();
    }

    public static class WriteExecution implements WriteToUri {
        private final Client client;
        private final Object value;

        public WriteExecution(Client client, Object value) {
            this.client = client;
            this.value = value;
        }

        @Override
        public WriteInTransaction toUri(String uri) {
            return new WriteOperation(client, value, uri);
        }
    }

    /* package */ static class WriteOperation implements WriteInTransaction {
        private final Client client;
        private final Object value;
        private final String uri;
        private Transaction transaction;
        private List<String> collections;
        private Transform transform;
        private Triples triples;

        public WriteOperation(Client client, Object value, String uri) {
            this.client = client;
            this.value = value;
            this.uri = uri;
        }

        @Override
        public WriteWithCollections inTransaction(Transaction transaction) {
            this.transaction = transaction;
            return this;
        }

        @Override
        public WriteWithTransform inCollections(String... names) {
            this.collections = Arrays.asList(names);
            return this;
        }

        @Override
        public WriteWithTriples transformedBy(Transform transform) {
            this.transform = transform;
            return this;
        }

        @Override
        public WriteMethod withTriples(Triples triples) {
            this.triples = triples;
            return this;
        }

        @Override
        public void asText() {
            client.textWriter.write(this);
        }

        @Override
        public void asJson() {
            // TODO impl
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void asXml() {
            // TODO impl
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public String toString() {
            return "UriWriteDestination{" +
                    "collections=" + collections +
                    ", value=" + value +
                    ", uri='" + uri + '\'' +
                    ", transaction=" + transaction +
                    ", transform=" + transform +
                    ", triples=" + triples +
                    '}';
        }

        public List<String> getCollections() {
            return collections;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public Transform getTransform() {
            return transform;
        }

        public Triples getTriples() {
            return triples;
        }

        public String getUri() {
            return uri;
        }

        public Object getValue() {
            return value;
        }
    }
}
