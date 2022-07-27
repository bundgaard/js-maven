package org.tretton63.eval;

import org.tretton63.ast.Expression;
import org.tretton63.lexer.Token;

import java.util.List;

public class CallExpression implements Expression {
    private Token token;
    private Expression function;
    private List<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
    }

    public void setArguments(List<Expression> newArguments) {
        this.arguments = newArguments;
    }
}
