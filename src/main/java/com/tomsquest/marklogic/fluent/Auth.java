package com.tomsquest.marklogic.fluent;

import java.util.Locale;

public class Auth {
    private final Scheme scheme;
    private final String host;
    private final int port;
    private final String user;
    private final String pass;
    private final AuthMethod authMethod;
    private final String realm;
    private final String serverUrl;

    public Auth(Scheme scheme, String host, int port, String user, String pass, AuthMethod authMethod, String realm) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.authMethod = authMethod;
        this.realm = realm;
        this.serverUrl = scheme.name().toLowerCase(Locale.ENGLISH) + "://" + host + ":" + port;
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public String getHost() {
        return host;
    }

    public String getPass() {
        return pass;
    }

    public int getPort() {
        return port;
    }

    public String getRealm() {
        return realm;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "authMethod=" + authMethod +
                ", scheme=" + scheme +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", realm='" + realm + '\'' +
                '}';
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public enum AuthMethod {
        DIGEST
    }

    public enum Scheme {
        HTTP
    }
}
