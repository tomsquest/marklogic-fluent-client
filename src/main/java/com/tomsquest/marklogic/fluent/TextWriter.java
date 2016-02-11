package com.tomsquest.marklogic.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;

public class TextWriter implements Writer {

    private final Config config;

    public TextWriter(Config config) {
        this.config = config;
    }

    @Override
    public void write(Client.WriteOperation writeOperation) {
        try {
            URIBuilder uriBuilder = new URIBuilder(config.getUrl());
            uriBuilder.addParameter("uri", writeOperation.getUri());

            if (writeOperation.getCollections() != null) {
                writeOperation.getCollections().forEach(coll -> uriBuilder.addParameter("collection", coll));
            }

            // TODO transaction ID + HostID
            // TODO transform + params
            // TODO triples

            Request request = Request
                    .Put(uriBuilder.build())
                    .bodyString(
                            writeOperation.getValue().toString(),
                            ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

            HttpResponse response = Executor
                    .newInstance()
                    .auth(AuthScope.ANY, new UsernamePasswordCredentials(
                            config.getUser(),
                            config.getPass())
                    )
                    .execute(request)
                    .returnResponse();

            System.out.println("response = " + response.getStatusLine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
