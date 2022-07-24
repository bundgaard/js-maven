package org.tretton63;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tretton63.lexer.Token;

public class TokenTest {

    @Test
    void testToken() {
        Assertions.assertEquals(Token.CloseBracket, "]");
    }
}
