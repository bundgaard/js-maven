package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class VariableStatement implements Statement {
    private final Token token;
    private final Identifier name;
    private final Expression value;

    public VariableStatement(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        out.append(name.toString());
        out.append(" = ");
        if (value != null) {
            out.append(value.toString());
        }
        return out.toString();
    }
}
