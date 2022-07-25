package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class NumberLiteral extends AbstractExpression {

    public NumberLiteral(Token token, String value) {
        super(token, value);
    }
}
