package org.tretton63.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenTest {

    @Test
    void testToken() {
        var token = new Token("=", Type.Equal);
        Assertions.assertEquals(Type.Equal, token.type());
    }
}
