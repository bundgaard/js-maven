package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class IndexExpression implements Expression {

    private Token token;

    private Expression left;
    private Expression index;

    public IndexExpression(Token token, Expression left) {
        this.token = token;
        this.left = left;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Expression getIndex() {
        return index;
    }

    public void setIndex(Expression index) {
        this.index = index;
    }
}
