package com.tomsquest.marklogic.fluent;

import org.apache.http.impl.client.CloseableHttpClient;

public class Config {

    private final Auth auth;

    private Config(Auth.Scheme scheme, String host, int port, String user, String pass, Auth.AuthMethod method) {
        this.auth = new Auth(scheme, host, port, user, pass, method, "public");
    }

    public static Config digest(String host, int port, String user, String pass) {
        return new Config(Auth.Scheme.HTTP, host, port, user, pass, Auth.AuthMethod.DIGEST);
    }

    public Auth getAuth() {
        return auth;
    }

    public CloseableHttpClient buildHttpClient() {
        return new HttpClientBuilder().build(auth);
    }

    public TextWriterBuilder getTextWriterBuilder() {
        return new TextWriterBuilder();
    }
}
