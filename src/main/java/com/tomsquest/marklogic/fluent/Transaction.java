package com.tomsquest.marklogic.fluent;

public class Transaction {

    private final Client client;

    public Transaction(Client client) {
        this.client = client;
    }

    public void commit() {
        System.out.println("Transaction.commit");
    }

    public void rollback() {
        System.out.println("Transaction.rollback");
    }
}
