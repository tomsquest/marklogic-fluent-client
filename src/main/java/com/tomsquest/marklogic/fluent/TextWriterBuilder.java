package com.tomsquest.marklogic.fluent;

public class TextWriterBuilder {

    public TextWriter build(Client client) {
        return new TextWriter(client);
    }
}
