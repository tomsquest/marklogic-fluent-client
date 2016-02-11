package com.tomsquest.marklogic.fluent;

public class Triple {
    public final String subject;
    public final String predicate;
    public final Object object;

    public Triple(String subject, String predicate, Object object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public String toString() {
        return "Triple{subject='" + subject + '\'' + ", predicate='" + predicate + '\'' + ", object=" + object + "}";
    }
}
