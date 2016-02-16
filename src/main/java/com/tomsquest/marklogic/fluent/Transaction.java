package com.tomsquest.marklogic.fluent;

public class Transaction {

    private final Client client;
    private final String transactionId;
    private final String hostId;

    public Transaction(Client client, String transactionId, String hostId) {
        this.client = client;
        this.transactionId = transactionId;
        this.hostId = hostId;
    }

    public void commit() {
        System.out.println("Transaction.commit");
    }

    public void rollback() {
        System.out.println("Transaction.rollback");
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", hostId='" + hostId + '\'' +
                '}';
    }
}
