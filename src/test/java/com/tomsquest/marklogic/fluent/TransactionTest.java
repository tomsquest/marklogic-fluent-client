package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionTest {

    Client client = mock(Client.class);
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);

    @Before
    public void setUp() throws Exception {
        given(client.getHttpClient()).willReturn(httpClient);
        given(httpClient.execute(any(HttpUriRequest.class), any(HttpContext.class))).willReturn(response);
    }

    @Test
    public void open_extracts_transaction_id_and_hostId() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_SEE_OTHER));
        given(response.getFirstHeader("Location")).willReturn(new BasicHeader("Location", "/v1/transactions/123"));
        given(response.getFirstHeader("Set-Cookie")).willReturn(new BasicHeader("Cookie", "HostId=345"));

        Transaction transaction = new Transaction(client).open();

        assertThat(transaction.getId()).isNotEmpty();
        assertThat(transaction.getHostId()).isNotEmpty();
    }

    @Test
    public void commit_transmits_transaction_id_and_hostId() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_NO_CONTENT));
        Transaction transaction = Transaction.openedTransaction(client, "123", "345");

        transaction.commit();

        verify(httpClient).execute(assertArg((req) -> {
            assertThat(req.getMethod()).isEqualTo("POST");
            assertThat(req.getURI().toString()).contains("/v1/transactions/123");
            assertThat(req.getFirstHeader("Cookie").getValue()).isEqualTo("HostId=345");
        }), any(HttpContext.class));
    }

    @Test
    public void rollback_transmits_transaction_id_and_hostId() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_NO_CONTENT));
        Transaction transaction = Transaction.openedTransaction(client, "123", "345");

        transaction.rollback();

        verify(httpClient).execute(assertArg((req) -> {
            assertThat(req.getMethod()).isEqualTo("POST");
            assertThat(req.getURI().toString()).contains("/v1/transactions/123");
            assertThat(req.getFirstHeader("Cookie").getValue()).isEqualTo("HostId=345");
        }), any(HttpContext.class));
    }

    private BasicStatusLine statusLine(int httpStatus) {
        return new BasicStatusLine(mock(ProtocolVersion.class), httpStatus, "reason");
    }
}