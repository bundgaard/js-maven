package org.tretton63.eval;

public class NumberObject extends JSObject {
    private long value;

    public NumberObject(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String toString() {
        return "Number(value="+value+")";
    }
}
