package com.tomsquest.marklogic.fluent;

import org.junit.Before;
import org.junit.Test;

public class ClientTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client(new Config(Config.Scheme.HTTP, "localhost", 8010, "admin", "admindev", Config.AuthMethod.DIGEST));
    }

    @Test
    public void write_simple_string() throws Exception {
        client.write("some text").toUri("/foo").asString();
    }

    @Test
    public void write_with_all_options_as_string() throws Exception {
        Transaction transaction = client.openTransaction();
        try {
            client.write("foobar")
                    .toUri("/foo")
                    .inTransaction(transaction)
                    .inCollections("foos", "bars")
                    .transformedBy(new Transform("clean"))
                    .withTriples(new Triples("s", "p", "o"))
                    .asString();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    @Test
    public void two_writes_in_single_transaction_given_transaction_opened_by_client() throws Exception {
        client.inTransaction((tran) -> {
            client.write("{}").toUri("/foo").inTransaction(tran).asString();
            client.write("{}").toUri("/bar").inTransaction(tran).asString();
        });
    }

    @Test
    public void two_writes_in_single_transaction_given_manually_opened_transaction() throws Exception {
        Transaction tran = client.openTransaction();
        try {
            client.write("{}").toUri("/foo").inTransaction(tran).asString();
            client.write("{}").toUri("/bar").inTransaction(tran).asString();
            tran.commit();
        } catch (Exception e) {
            tran.rollback();
        }
    }
}