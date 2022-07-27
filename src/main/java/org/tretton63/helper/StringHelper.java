package org.tretton63.helper;

public class StringHelper {

    public static String repeat(String seq, int count) {
        var out = new StringBuilder();
        out.append(String.valueOf(seq).repeat(Math.max(0, count)));
        return out.toString();
    }
}
