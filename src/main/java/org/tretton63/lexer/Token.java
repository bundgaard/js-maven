package org.tretton63.lexer;

public class Token {
    private String value;
    private Type type;

    public Token(String value, Type type) {
        this.type = type;
        this.value = value;
    }

    public Token() {

    }

    public void setType(Type newType) {
        this.type = newType;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String value() {
        return value;
    }
    public Type type() {
        return type;
    }
}

