package org.tretton63.ast;

import org.tretton63.lexer.Token;

import java.util.List;

public class CallExpression implements Expression {
    private Token token;
    private Expression function;
    private List<Expression> arguments;

    public Token getToken() {
        return token;
    }

    public Expression getFunction() {
        return function;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
    }

    public void setArguments(List<Expression> newArguments) {
        this.arguments = newArguments;
    }
}
