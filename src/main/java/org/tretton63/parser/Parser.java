package org.tretton63.parser;

import org.tretton63.ast.Identifier;
import org.tretton63.ast.*;
import org.tretton63.eval.BlockStatement;
import org.tretton63.eval.CallExpression;
import org.tretton63.eval.FunctionObject;
import org.tretton63.lexer.JSLexer;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tretton63.ast.Priority.Lowest;
import static org.tretton63.lexer.Type.Number;
import static org.tretton63.lexer.Type.String;
import static org.tretton63.lexer.Type.*;

import java.lang.String;

public class Parser extends AbstractParser {

    private static final Map<Type, PrefixParser> prefixParserMap = new HashMap<>();
    private static final Map<Type, InfixParser> infixParserMap = new HashMap<>();

    public Parser(String input) {
        super(new JSLexer(input));

        prefixParserMap.put(Identifier, new NameParselet());
        prefixParserMap.put(String, new StringParselet());
        prefixParserMap.put(Number, new NumberParselet());
        prefixParserMap.put(OpenCurly, new HashParselet());
        prefixParserMap.put(OpenBracket, new ArrayParselet());
        prefixParserMap.put(Function, new FunctionParselet());
        prefixParserMap.put(OpenParen, new GroupedExpressionParselet());

        infixParserMap.put(Plus, new InfixExpressionParselet());
        infixParserMap.put(Minus, new InfixExpressionParselet());
        infixParserMap.put(Equal, new InfixExpressionParselet());
        infixParserMap.put(Multiply, new InfixExpressionParselet());
        infixParserMap.put(Divide, new InfixExpressionParselet());
        infixParserMap.put(Percentage, new InfixExpressionParselet());
        infixParserMap.put(OpenParen, new CallExpressionParselet());

        // IndexExpression and CallExpression

        nextToken();
        nextToken();
    }

    public Program parse() {
        var p = new Program();
        while (current().type() != Type.EOF) {
            var statement = parseStatement();
            if (statement != null) {
                p.add(statement);
            }
            nextToken();
        }
        return p;
    }

    private Statement parseStatement() {
        return switch (current().type()) {
            case Var -> parseVariable();
            case CommentBlock, CommentLine -> null;
            default -> parseExpressionStatement();
        };
    }

    private Statement parseVariable() {
        var varToken = current();
        if (!expectPeek(Type.Identifier)) {
            return null;
        }

        var name = new Identifier(current(), current().value());
        if (!expectPeek(Equal)) {
            return null;
        }
        nextToken();

        var value = parseExpression(Lowest);
        if (peekTokenIs(Type.Semi)) {
            nextToken();
        }
        return new VariableStatement(varToken, name, value);
    }

    private ExpressionStatement parseExpressionStatement() {
        var statement = new ExpressionStatement(current());
        statement.setExpression(parseExpression(Lowest));

        if (peekTokenIs(Dot)) {
            nextToken();
            var right = parseInfixExpression(statement.getExpression());
            statement.setExpression(right);
        }

        if (peekTokenIs(Equal)) {
            nextToken();
            var right = parseInfixExpression(statement.getExpression());
            statement.setExpression(right);
        }

        if (peekTokenIs(Semi)) {
            nextToken();
        }
        return statement;
    }

    private Expression parseExpression(Priority priority) {
        if (peekTokenIs(CommentLine) || peekTokenIs(CommentBlock)) {
            nextToken();
        }
        var prefix = prefixParserMap.get(current().type());
        if (prefix == null) {
            return null;
        }

        var left = prefix.apply(this, current());
        while (!peekTokenIs(Semi) && priority.compareTo(peekPrecedence()) < 0) {
            var infix = infixParserMap.get(next().type());
            if (infix == null) {
                return left;
            }
            nextToken();
            left = infix.apply(this, left, current());
        }
        return left;
    }

    private Expression parseInfixExpression(Expression left) {
        var expression = new InfixExpression(current(), left, current().value());
        var precedence = currentPrecedence();
        nextToken();
        expression.setRight(parseExpression(precedence));
        return expression;
    }

    private List<Expression> parseExpressionList(Type end) {
        var list = new ArrayList<Expression>();
        if (peekTokenIs(end)) {
            nextToken();
            return list;
        }

        nextToken();
        list.add(parseExpression(Lowest));

        while (peekTokenIs(Comma)) {
            nextToken(); // Eat Comma
            nextToken(); // Expression
            list.add(parseExpression(Lowest));
        }

        if (!expectPeek(end)) {
            return null;
        }
        return list;
    }

    private List<Identifier> parseFunctionArguments() {
        var list = new ArrayList<Identifier>();
        if (peekTokenIs(CloseParen)) {
            nextToken();
            return list;
        }

        nextToken(); // EAT OpenParen;
        var identifier = new Identifier(current(), current().value());
        list.add(identifier);
        while (peekTokenIs(Comma)) {
            nextToken();
            nextToken();
            identifier = new Identifier(current(), current().value());
            list.add(identifier);
        }

        if (!expectPeek(CloseParen)) {
            return null;
        }
        return list;
    }

    private class GroupedExpressionParselet implements PrefixParser {

        @Override
        public Expression apply(Parser parser, Token token) {
            nextToken();
            var expression = parseExpression(Lowest);
            if (!expectPeek(CloseParen)) {
                return null;
            }
            return expression;
        }
    }

    private class CallExpressionParselet implements InfixParser {

        @Override
        public Expression apply(Parser parser, Expression left, Token token) {

            var call = new CallExpression(current(), left);
            call.setArguments(parseExpressionList(CloseParen));
            return call;
        }
    }

    private class InfixExpressionParselet implements InfixParser {

        @Override
        public Expression apply(Parser parser, Expression left, Token token) {
            var expression = new InfixExpression(current(), left, current().value());
            var curPrecedence = currentPrecedence();
            nextToken();
            expression.setRight(parseExpression(curPrecedence));
            return expression;
        }
    }

    private BlockStatement parseBlockStatement() {
        var block = new BlockStatement(current());
        nextToken();

        while (!currentTokenIs(CloseCurly) && !currentTokenIs(EOF)) {
            var statement = parseStatement();
            if (statement != null) {
                block.addStatement(statement);
            }
            nextToken();
        }
        return block;
    }

    private class FunctionParselet implements PrefixParser {

        @Override
        public Expression apply(Parser parser, Token token) {
            var function = new FunctionObject(current());
            nextToken();
            function.setName(current().value());
            if (!expectPeek(OpenParen)) {
                return null;
            }
            function.setParameters(parseFunctionArguments());
            if (!expectPeek(OpenCurly)) {
                return null;
            }
            function.setBody(parseBlockStatement());
            return function;
        }
    }

    private class ArrayParselet implements PrefixParser {

        @Override
        public Expression apply(Parser parser, Token token) {
            var array = new ArrayLiteral(token);
            array.setElements(parseExpressionList(CloseBracket));
            return array;
        }
    }

    private class HashParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            var hash = new HashLiteral(token);
            while (!peekTokenIs(CloseCurly)) {
                nextToken(); // eat open curly
                var key = parseExpression(Lowest);
                if (!expectPeek(Colon)) {
                    return null;
                }
                nextToken();
                var value = parseExpression(Lowest);
                hash.add(key, value);
                if (!peekTokenIs(CloseCurly) && !expectPeek(Comma)) {
                    return null;
                }
            }
            return hash;
        }
    }

    private static class NameParselet implements PrefixParser {

        @Override
        public Expression apply(Parser parser, Token token) {
            return new Identifier(token, token.value());
        }
    }

    private static class NumberParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            return new NumberLiteral(token, token.value());
        }
    }

    private static class StringParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            return new StringLiteral(token, token.value());
        }
    }


    //////////////////////////////


}
