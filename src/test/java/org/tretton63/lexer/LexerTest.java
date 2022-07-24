package org.tretton63.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    @Test
    void testLexer() {
        var lexer = new Lexer("100+3");
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
    void testStringToken() {
        var lexer = new Lexer("      \"Hello, World\"");
        var token = lexer.nextToken();
        assertEquals("\"Hello, World\"", token.value());
    }
}
