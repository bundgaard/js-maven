package org.tretton63.eval;

import org.tretton63.ast.Expression;
import org.tretton63.ast.Identifier;
import org.tretton63.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionObject implements Expression {
    private final Token token;
    private String name;
    private List<Identifier> parameters;
    private BlockStatement body;

    public FunctionObject(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public void setParameters(List<Identifier> parameters) {
        this.parameters = parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    public String toString() {
        var out = new StringBuilder();
        out.append("function");
        if (name != null && !(name.equalsIgnoreCase(""))) {
            out.append(" ").append(name).append(" ");
        }

        out.append("(");
        out.append(parameters.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
        out.append(")");
        out.append(body.toString());
        return out.toString();
    }
}
