package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class ReturnExpression implements Expression {
    private final Token token;

    private Expression expression;

    public ReturnExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
