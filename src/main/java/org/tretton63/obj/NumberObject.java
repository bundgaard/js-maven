package org.tretton63.obj;

public class NumberObject implements JSObject {
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
