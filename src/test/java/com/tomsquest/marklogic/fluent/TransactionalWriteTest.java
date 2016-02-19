package com.tomsquest.marklogic.fluent;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionalWriteTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client(new Config(Config.Scheme.HTTP, "localhost", 8010, "admin", "admindev", Config.AuthMethod.DIGEST));
        client.delete("/foo");
    }

    @Test
    public void commit_manually() throws Exception {
        Transaction transaction = client.openTransaction();
        client.write("foo").toUri("/foo").inTransaction(transaction).asString();

        transaction.commit();

        assertThat(client.exists("/foo")).isTrue();
    }

    @Test
    public void rollback_manually() throws Exception {
        Transaction transaction = client.openTransaction();
        client.write("foo").toUri("/foo").inTransaction(transaction).asString();

        transaction.rollback();

        assertThat(client.exists("/foo")).isFalse();
    }

    @Test
    public void inTransaction_commit() throws Exception {
        client.inTransaction((tran) -> {
            client.write("{}").toUri("/foo").inTransaction(tran).asString();
        });

        assertThat(client.exists("/foo")).isTrue();
    }

    @Test
    public void inTransaction_rollback() throws Exception {
        client.inTransaction((tran) -> {
            tran.rollback(); // make it fail

            client.write("{}").toUri("/foo").inTransaction(tran).asString();
        });

        assertThat(client.exists("/foo")).isFalse();
    }
}