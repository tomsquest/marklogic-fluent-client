package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DeleteCommandTest {

    Client client = mock(Client.class);
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);

    @Before
    public void setUp() throws Exception {
        given(client.getHttpClient()).willReturn(httpClient);
        given(httpClient.execute(any(HttpUriRequest.class), any(HttpContext.class))).willReturn(response);
    }

    @Test
    public void delete() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_NO_CONTENT));

        new DeleteCommand(client, "/test").run();

        verify(httpClient).execute(assertArg(req -> {
            assertThat(req.getMethod()).isEqualTo("DELETE");
            assertThat(req.getURI().toString()).contains("/v1/documents?uri=%2Ftest");
        }), any(HttpContext.class));
    }

    @Test
    public void server_error() throws Exception {
        given(response.getStatusLine()).willReturn(statusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> new DeleteCommand(client, "/test").run())
                .isInstanceOf(UnableToDeleteDocumentsException.class);
    }

    @Test
    public void httpClient_throws_an_exception() throws Exception {
        given(httpClient.execute(any(HttpUriRequest.class), any(HttpContext.class))).willThrow(new IOException("TEST"));

        assertThatThrownBy(() -> new DeleteCommand(client, "/test").run())
                .isInstanceOf(UnableToDeleteDocumentsException.class);
    }

    private BasicStatusLine statusLine(int httpStatus) {
        return new BasicStatusLine(new ProtocolVersion("http", 1, 1), httpStatus, "reason");
    }
}