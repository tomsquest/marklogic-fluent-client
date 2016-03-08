package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TextWriterTest {

    Client client = mock(Client.class);
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    TextWriter writer = new TextWriter(client);

    @Before
    public void setUp() throws Exception {
        given(client.getHttpClient()).willReturn(httpClient);
        given(httpClient.execute(any(HttpUriRequest.class), any(HttpContext.class))).willReturn(response);
    }

    @Test
    public void simple_write() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_CREATED));
        Client.WriteOperation op = new Client.WriteOperation(client, "text", "/test");

        writer.write(op);

        verify(httpClient).execute(assertArg(req -> {
            assertThat(req.getMethod()).isEqualTo("PUT");
            assertThat(req.getURI().toString()).contains("/v1/documents?uri=%2Ftest");
            HttpEntityEnclosingRequest put = (HttpEntityEnclosingRequest) req;
            assertThat(getEntityAsText(put)).contains("text");
        }), any(HttpContext.class));
    }

    @Test
    public void write_with_parameters() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_CREATED));
        Transaction transaction = Transaction.openedTransaction(client, "123", "345");
        Client.WriteOperation op = new Client.WriteOperation(client, "text", "/test");
        op.inTransaction(transaction);
        op.inCollections("foos", "bars");

        writer.write(op);

        verify(httpClient).execute(assertArg(req -> {
            assertThat(req.getURI().toString()).contains("collection=foos");
            assertThat(req.getURI().toString()).contains("collection=bars");
            assertThat(req.getURI().toString()).contains("txid=123");
            assertThat(req.getFirstHeader("Cookie").getValue()).isEqualTo("HostId=345");
        }), any(HttpContext.class));
    }

    @Test
    public void server_responds_with_an_error() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Client.WriteOperation op = new Client.WriteOperation(client, "text", "/test");

        assertThatThrownBy(() -> writer.write(op))
                .isInstanceOf(UnableToWriteException.class);
    }

    @Test
    public void httpClient_throws_an_exception() throws Exception {
        given(httpClient.execute(any(HttpUriRequest.class), any(HttpContext.class))).willThrow(new IOException("TEST ERROR"));
        Client.WriteOperation op = new Client.WriteOperation(client, "text", "/test");

        assertThatThrownBy(() -> writer.write(op))
                .isInstanceOf(UnableToWriteException.class);
    }

    private String getEntityAsText(HttpEntityEnclosingRequest put) {
        try {
            return EntityUtils.toString(put.getEntity());
        } catch (IOException e) {
            fail("Unable to read request as a String", e);
            return null;
        }
    }

    private BasicStatusLine statusLine(int httpStatus) {
        return new BasicStatusLine(new ProtocolVersion("http", 1, 1), httpStatus, "reason");
    }
}