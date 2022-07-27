package org.tretton63.eval;

public class StringObject extends JSObject {
    private final String value;
    public StringObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "String(" +
                "value='" + value + '\'' +
                ')';
    }
}
