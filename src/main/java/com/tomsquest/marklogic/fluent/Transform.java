package com.tomsquest.marklogic.fluent;

public class Transform {
    public final String name;

    public Transform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Transform{name='" + name + '\'' + '}';
    }
}
