package org.tretton63.obj;

public class JSError extends AbstractJSObject {

    private final String message;

    public JSError(String message) {
        this.message = message;
    }

    public String toString() {
        return "JSError(message=" + message + ")";
    }
}
