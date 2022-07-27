package org.tretton63.parser;

import org.junit.jupiter.api.Test;
import org.tretton63.ast.Identifier;
import org.tretton63.ast.NumberLiteral;
import org.tretton63.ast.Program;
import org.tretton63.ast.VariableStatement;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test void testFunctionBlock() {
        var parser = new Parser("""
                function hej() {
                (1+2)*10 / 2;
                }""");
        var program = parser.parse();
        System.out.println(program);
    }
}