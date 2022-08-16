package org.tretton63.lexer;

import org.junit.jupiter.api.Test;
import org.tretton63.js.JSLexer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JSLexerTest {

    @Test
    void testLexer() {
        var lexer = new JSLexer("100+3");
        var token = lexer.nextToken();

        assertEquals(Type.Number, token.type());
        assertEquals("100", token.value());

        token = lexer.nextToken();
        assertEquals(Type.Plus, token.type());
        assertEquals("+", token.value());


        token = lexer.nextToken();
        assertEquals(Type.Number, token.type());
        assertEquals("3", token.value());


    }

    @Test
    void testCommentBlock() {
        var expectedTokens = List.of(
                new Token("""
                        /* Hello World
                        */""", Type.CommentBlock)
        );

        var lexer = new JSLexer("""
                /* Hello World
                */""");
        var token = lexer.nextToken();
        var current = 0;
        while (token.type() != Type.EOF) {
            assertEquals(expectedTokens.get(current).value(), token.value(), token.value());
            assertEquals(Type.CommentBlock, expectedTokens.get(current).type());
            current++;
            token = lexer.nextToken();
        }
    }

    @Test
    void testCommentLine() {
        var expectedTokens = List.of(new Token("// Hello World", Type.CommentLine));
        var current = 0;
        var lexer = new JSLexer("// Hello World");
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(expectedTokens.get(current).type(), Type.CommentLine);
            current++;
            token = lexer.nextToken();
        }
    }

    @Test
    void testCompleteProgram() {
        var input = """
                // Hello World
                /* Another comment
                */
                                
                var a = 1;
                var b = 2;
                var c = a + b;
                """;
        var expectedTokens = List.of(
                new Token("// Hello World", Type.CommentLine),
                new Token("/* Another comment\n*/", Type.CommentBlock),

                new Token("var", Type.Var),
                new Token("a", Type.Identifier),
                new Token("=", Type.Equal),
                new Token("1", Type.Number),
                new Token(";", Type.Semi),
                new Token("var", Type.Var),
                new Token("b", Type.Identifier),
                new Token("=", Type.Equal),
                new Token("2", Type.Number),
                new Token(";", Type.Semi),
                new Token("var", Type.Var),
                new Token("c", Type.Identifier),
                new Token("=", Type.Equal),
                new Token("a", Type.Identifier),
                new Token("+", Type.Plus),
                new Token("b", Type.Identifier),
                new Token(";", Type.Semi)
        );
        var lexer = new JSLexer(input);
        var token = lexer.nextToken();
        var sameCounter = 0;
        var current = 0;
        Token previous = null;
        while (token.type() != Type.EOF) {
            if (previous != null && previous.equals(token) && sameCounter == 10) {
                break;
            }
            assertEquals(expectedTokens.get(current).type(), token.type(), token.toString());
            current++;
            sameCounter++;
            previous = token;
            token = lexer.nextToken();
        }
    }

    @Test
    void testKeywords() {
        var expectedTokens = List.of(
                new Token("var", Type.Var),
                new Token("function", Type.Function),
                new Token("null", Type.Null),
                new Token("undefined", Type.Undefined),
                new Token("true", Type.True),
                new Token("false", Type.False));

        var lexer = new JSLexer("     var function null undefined true false");
        int current = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(token.hashCode(), expectedTokens.get(current).hashCode());
            assertEquals(token, expectedTokens.get(current), "expected token");
            assertNotNull(token.toString());
            current++;
            token = lexer.nextToken();
        }
    }

    @Test
    void testUndefined() {
        var lexer = new JSLexer("undefined");
        var token = lexer.nextToken();
        assertEquals(Type.Undefined, token.type());
    }

    @Test
    void testStringToken() {
        var lexer = new JSLexer("      \"Hello, World\"");
        var token = lexer.nextToken();
        assertEquals("\"Hello, World\"", token.value());
    }

    @Test
    void testEOF() {
        var lexer = new JSLexer("");
        var token = lexer.nextToken();
        assertEquals(Type.EOF, token.type());
    }

    @Test
    void testMathOperators() {
        var expectedTokens = List.of(
                new Token("+", Type.Plus),
                new Token("-", Type.Minus),
                new Token("/", Type.Divide),
                new Token("*", Type.Multiply),
                new Token("%", Type.Percentage));

        var lexer = new JSLexer("+ - / * %");
        int i = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(token, expectedTokens.get(i++), "expected token");
            token = lexer.nextToken();
        }
    }

    @Test
    void testBrackets() {
        var expectedTokens = List.of(
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token("[", Type.OpenBracket),
                new Token("]", Type.CloseBracket),
                new Token("{", Type.OpenCurly),
                new Token("}", Type.CloseCurly),
                new Token(",", Type.Comma),
                new Token(".", Type.Dot),
                new Token(";", Type.Semi));

        var current = 0;
        var lexer = new JSLexer("()[]{},.;");
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(expectedTokens.get(current), token);
            current++;
            token = lexer.nextToken();
        }
    }

    @Test
    void testFunctionTokenBlock() {
        var expectedTokens = List.of(
                new Token("function", Type.Function),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token("{", Type.OpenCurly),
                new Token("\"hej\"", Type.String),
                new Token(";", Type.Semi),
                new Token("}", Type.CloseCurly)
        );
        var lexer = new JSLexer("""
                function hej() {
                "hej";
                }""");
        var current = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(expectedTokens.get(current).type(), token.type(), token.value());
            token = lexer.nextToken();
            current++;
        }
    }

    @Test
    void testFunctionTokenBlockWithArguments() {
        var expectedTokens = List.of(
                new Token("function", Type.Function),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token("a", Type.Identifier),
                new Token(",", Type.Comma),
                new Token("b", Type.Identifier),
                new Token(",", Type.Comma),
                new Token("c", Type.Identifier),
                new Token(")", Type.CloseParen),
                new Token("{", Type.OpenCurly),
                new Token("\"hej\"", Type.String),
                new Token(";", Type.Semi),
                new Token("}", Type.CloseCurly)
        );
        var lexer = new JSLexer("""
                function hej(a,b,c) {
                "hej";
                }""");
        var current = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            assertEquals(expectedTokens.get(current).type(), token.type(), token.value());
            token = lexer.nextToken();
            current++;
        }


    }


    @Test
    void testFunctionBlock() {
        var expectedTokens = List.of(
                new Token("function", Type.Function),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token("{", Type.OpenCurly),
                new Token("1", Type.Number),
                new Token("+", Type.Plus),
                new Token("1", Type.Number),
                new Token(";", Type.Semi),
                new Token("}", Type.CloseCurly),
                new Token("println", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token(")", Type.CloseParen),
                new Token(";", Type.Semi)


        );
        var lexer = new JSLexer("""
                function hej() {
                1+1;
                }
                                
                println(hej());""");

        var current = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            System.out.println(token);
            assertEquals(expectedTokens.get(current).type(), token.type());
            current++;
            token = lexer.nextToken();
        }
    }

    @Test
    void testReturnFunctionBlock() {
        var expectedTokens = List.of(
                new Token("function", Type.Function),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token("{", Type.OpenCurly),
                new Token("return", Type.Return),
                new Token("1", Type.Number),
                new Token(";", Type.Semi),
                new Token("}", Type.CloseCurly),
                new Token("println", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token("hej", Type.Identifier),
                new Token("(", Type.OpenParen),
                new Token(")", Type.CloseParen),
                new Token(")", Type.CloseParen),
                new Token(";", Type.Semi)


        );
        var lexer = new JSLexer("""
                function hej() {
                return 1;
                }
                                
                println(hej());""");
        var current = 0;
        var token = lexer.nextToken();
        while (token.type() != Type.EOF) {
            System.out.println(token);
            assertEquals(expectedTokens.get(current).type(), token.type());
            current++;
            token = lexer.nextToken();
        }
    }
}
