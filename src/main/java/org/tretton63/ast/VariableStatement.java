package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class VariableStatement implements Statement {
    private final Token token;
    private final Expression left, right;

    public VariableStatement(Token token, Expression left, Expression right) {
        this.token = token;
        this.left = left;
        this.right = right;
    }
}
