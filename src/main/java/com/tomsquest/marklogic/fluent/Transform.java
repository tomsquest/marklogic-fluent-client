package com.tomsquest.marklogic.fluent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
