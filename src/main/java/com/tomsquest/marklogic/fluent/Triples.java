package com.tomsquest.marklogic.fluent;

import java.util.ArrayList;
import java.util.List;

public class Triples {
    public final List<Triple> values = new ArrayList<>();

    public Triples() {
    }

    /** Convenient constructor to create a new Triples with a single Triple */
    public Triples(String subject, String predicate, Object object) {
        this.values.add(new Triple(subject, predicate, object));
    }

    @Override
    public String toString() {
        return "Triples{" + values + "}";
    }
}
