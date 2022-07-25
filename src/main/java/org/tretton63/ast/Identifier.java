package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class Identifier extends AbstractExpression {

    public Identifier(Token token, String value) {
        super(token, value);
    }
}
