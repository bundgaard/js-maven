package org.tretton63.parser;

import org.junit.jupiter.api.Test;
import org.tretton63.ast.*;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

public class ParserTest {

    @Test
    void testParser() {
        var parser = new Parser("undefined");
        parser.toString();
    }


    @Test
    void testParseProgram() {
        var variableStatement = new VariableStatement(
                new Token("var", Type.Var),
                new Identifier(new Token("s", Type.Identifier), "s"),
                new NumberLiteral(new Token("1234", Type.Number), "1234"));
        var program = new Program();
        program.add(variableStatement);
        var parser = new Parser("var s = 1234");
        var actual = parser.parse();
        System.out.println(actual);
    }


    @Test
    void testParseInfix() {
        var parser = new Parser("var s = 12 + 1 * 6;");
        var program = parser.parse();
        System.out.println(program);
    }

    @Test
    void testParseXEqual100() {

        var parser = new Parser("var x = 100;");
        var token = parser.parse();
    }

    @Test
    void testParseVarWithModulo() {
        var parser = new Parser("var x = 100 % 10;");
        var token = parser.parse();
    }

    @Test
    void testFunctionBlock() {
        var expectedProgram = new Program();
        var functionToken = new Token("function", Type.Function);
        var statement = new ExpressionStatement(functionToken);
        var functionLiteral = new FunctionLiteral(functionToken);
        functionLiteral.setName("hej");
        var body = new BlockStatement(new Token("{", Type.OpenCurly));
        var newStatement = new ExpressionStatement(new Token("(", Type.OpenParen));
        newStatement.setExpression(
                new InfixExpression(new Token("/", Type.Divide),
                        new InfixExpression(new Token("", Type.Divide), null, ""),
                        ""));
        body.addStatement(newStatement);
        functionLiteral.setBody(body);
        statement.setExpression(functionLiteral);
        expectedProgram.add(statement);

        var parser = new Parser("""
                function hej() {
                (1+2)*10 / 2;
                }""");
        var program = parser.parse();
        System.out.println(program);
    }

    @Test
    void testReturnFunctionBlock() {

        var parser = new Parser("""
                function hej() {
                return 1;
                }
                                
                println(hej());
                """);
        var program = parser.parse();
        System.out.println(program);
    }
}