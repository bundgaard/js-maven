package org.tretton63.obj;

public class StringObject implements JSObject {
    private final String value;

    public StringObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "String(" +
                "value='" + value + '\'' +
                ')';
    }
}
