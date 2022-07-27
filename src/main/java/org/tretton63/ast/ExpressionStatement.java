package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class ExpressionStatement implements Statement, Expression {
    private final Token token;
    private Expression expression;

    public ExpressionStatement(Token token) {
        this.token = token;
    }

    public void setExpression(Expression newExpression) {
        this.expression = newExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public String toString() {
        if (expression != null) {
            return expression.toString();
        }
        return "<null>";
    }
}
