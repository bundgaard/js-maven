package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class InfixExpression implements Expression {
    private final Token token;
    private final Expression left;
    private final String operator;
    private Expression right;


    public InfixExpression(Token token, Expression left, String operator) {
        this.token = token;
        this.left = left;
        this.operator = operator;
    }

    public void setRight(Expression newRight) {
        this.right = newRight;
    }

    public String toString() {
        return "(" +
                left.toString() +
                " " + operator + " " +
                right.toString() +
                ")";
    }

    public Token getToken() {
        return token;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}
