package com.tomsquest.marklogic.fluent;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientBuilder {

    public CloseableHttpClient build(Auth auth) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(
                        auth.getHost(),
                        auth.getPort(),
                        auth.getRealm()),
                new UsernamePasswordCredentials(auth.getUser(), auth.getPass()));

        return HttpClients
                .custom()
                .disableRedirectHandling()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
    }
}
