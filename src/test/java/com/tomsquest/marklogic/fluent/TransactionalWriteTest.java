package com.tomsquest.marklogic.fluent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionalWriteTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client(Config.digest("localhost", 8010, "admin", "admindev"));
        client.delete("/foo", "/bar");
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void commit_manually() throws Exception {
        Transaction transaction = client.openTransaction();
        client.write("foo").toUri("/foo").inTransaction(transaction).asText();
        client.write("bar").toUri("/bar").inTransaction(transaction).asText();

        transaction.commit();

        assertThat(client.exists("/foo")).isTrue();
        assertThat(client.exists("/bar")).isTrue();
    }

    @Test
    public void rollback_manually() throws Exception {
        Transaction transaction = client.openTransaction();
        client.write("foo").toUri("/foo").inTransaction(transaction).asText();
        client.write("bar").toUri("/bar").inTransaction(transaction).asText();

        transaction.rollback();

        assertThat(client.exists("/foo")).isFalse();
        assertThat(client.exists("/bar")).isFalse();
    }

    @Test
    public void inTransaction_commit() throws Exception {
        client.inTransaction((tran) -> {
            client.write("foo").toUri("/foo").inTransaction(tran).asText();
            client.write("bar").toUri("/bar").inTransaction(tran).asText();
        });

        assertThat(client.exists("/foo")).isTrue();
        assertThat(client.exists("/bar")).isTrue();
    }

    @Test
    public void inTransaction_rollback() throws Exception {
        client.inTransaction((tran) -> {
            tran.rollback(); // make it fail

            client.write("{}").toUri("/foo").inTransaction(tran).asText();
        });

        assertThat(client.exists("/foo")).isFalse();
    }
}