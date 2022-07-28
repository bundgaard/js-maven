package org.tretton63.obj;

import org.tretton63.ast.BlockStatement;
import org.tretton63.ast.Identifier;
import org.tretton63.eval.Environment;
import org.tretton63.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionObject extends AbstractJSObject {
    private final Token token;
    private String name;
    private List<Identifier> parameters;
    private BlockStatement body;
    private final Environment environment;

    public FunctionObject(Token token, String name, List<Identifier> parameters, BlockStatement body, Environment environment) {
        this.token = token;
        this.environment = environment;
        this.name = name;
        this.body = body;
        this.parameters = parameters;
    }

    public Environment getEnvironment() {
        return environment;
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
