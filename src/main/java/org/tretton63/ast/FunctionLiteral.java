package org.tretton63.ast;

import org.tretton63.lexer.Token;

import java.util.List;

public class FunctionLiteral implements Expression {
    private final Token token;
    private String name;
    private BlockStatement body;
    private List<Identifier> parameters;

    public Token getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public void setParameters(List<Identifier> parameters) {
        this.parameters = parameters;
    }

    public FunctionLiteral(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "FunctionLiteral{" +
                "token=" + token +
                ", name='" + name + '\'' +
                ", body=" + body +
                ", parameters=" + parameters +
                '}';
    }
}
