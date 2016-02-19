package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Client {

    final Config config;

    public Client(Config config) {
        this.config = config;
    }

    public Transaction openTransaction() {
        Transaction transaction = new Transaction(this);
        return transaction.open();
    }

    public Client inTransaction(Consumer<Transaction> consumer) {
        Transaction tran = openTransaction();
        try {
            consumer.accept(tran);
            tran.commit();
        } catch (Exception e) {
            tran.rollback();
        }
        return this;
    }

    public boolean exists(String uri) {
        try {
            URIBuilder uriBuilder = new URIBuilder(config.getUrl() + "/LATEST/documents");
            uriBuilder.addParameter("uri", uri);

            HttpResponse response = Executor
                    .newInstance()
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            config.getUser(),
                            config.getPass())
                    )
                    .execute(Request.Head(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public Client delete(String... uris) {
        try {
            URIBuilder uriBuilder = new URIBuilder(config.getUrl() + "/LATEST/documents");
            Arrays.asList(uris).forEach(uri -> uriBuilder.addParameter("uri", uri));

            HttpResponse response = Executor
                    .newInstance()
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            config.getUser(),
                            config.getPass())
                    )
                    .execute(Request.Delete(uriBuilder.build()))
                    .returnResponse();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                // TODO log success
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        void asString();

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

    static class WriteOperation implements WriteInTransaction {
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
        public void asString() {
            client.config.getTextWriter().write(this);
        }

        @Override
        public void asJson() {
            client.config.getJsonWriter().write(this);
        }

        @Override
        public void asXml() {
            client.config.getXmlWriter().write(this);
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
