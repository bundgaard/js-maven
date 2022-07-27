package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class AbstractExpression implements Expression {
    private Token token;
    private String value;

    public AbstractExpression(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public void setToken(Token newToken) {
        this.token = newToken;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }


    public String toString() {
        return getClass().getSimpleName() + "(value=" + value + ")";
    }
}
