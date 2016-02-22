package com.tomsquest.marklogic.fluent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client(Config.digest("localhost", 8010, "admin", "admindev"));
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void write_simple_string() throws Exception {
        client.delete("/foo");
        client.write("some text").toUri("/foo").asText();
        assertThat(client.exists("/foo")).isTrue(); // TODO assert content written
    }

    @Test
    public void write_with_all_options_as_string() throws Exception {
        client.delete("/foo");

        Transaction transaction = client.openTransaction();
        client.write("foobar")
                .toUri("/foo")
                .inTransaction(transaction)
                .inCollections("foos", "bars")
                .transformedBy(new Transform("clean"))
                .withTriples(new Triples("s", "p", "o"))
                .asText();

        transaction.commit();

        assertThat(client.exists("/foo")).isTrue(); // TODO assert content written
    }
}