package org.tretton63.eval;

public class JSError extends JSObject {

    private final String message;

    public JSError(String message) {
        this.message = message;
    }

    public String toString() {
        return "JSError(message=" + message + ")";
    }
}
