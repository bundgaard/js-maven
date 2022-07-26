package org.tretton63.lexer;

import java.util.Objects;

public class Token {
    private String value;
    private Type type;

    public Token(String value, Type type) {
        this.type = type;
        this.value = value;
    }


    public String value() {
        return value;
    }
    public Type type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(value, token.value) && type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}

