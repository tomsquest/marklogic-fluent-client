package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class TransactionOpenerTest {

    @Test
    public void open_tran() throws Exception {
        Client client = new Client(new Config(Config.Scheme.HTTP, "localhost", 8010, "admin", "admindev", Config.AuthMethod.DIGEST));

        Transaction transaction = client.openTransaction();

        assertThat(transaction.getTransactionId()).isNotEmpty();
        assertThat(transaction.getHostId()).isNotEmpty();
    }
}