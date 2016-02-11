package com.tomsquest.marklogic.fluent;

import java.util.Locale;

public class Config {
    private final String url;
    private final String user;
    private final String pass;
    private final AuthMethod authMethod;

    public Config(Scheme scheme, String host, int port, String user, String pass, AuthMethod authMethod) {
        this.url = scheme.name().toLowerCase(Locale.ENGLISH) + "://" + host + ":" + port;
        this.user = user;
        this.pass = pass;
        this.authMethod = authMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Writer getTextWriter() {
        return new TextWriter(this);
    }

    public Writer getJsonWriter() {
        // TODO impl
        throw new UnsupportedOperationException("not implemented");
    }

    public Writer getXmlWriter() {
        // TODO impl
        throw new UnsupportedOperationException("not implemented");
    }

    public enum AuthMethod {
        DIGEST
    }

    public enum Scheme {
        HTTP
    }
}
