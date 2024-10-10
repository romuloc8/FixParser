package org.zhengzhixuan.fix.message;

public class FixFieldValue {
    private final String value;

    private FixFieldValue(String value) {
        this.value = value;
    }

    public static FixFieldValue create(String value) {
        return new FixFieldValue(value);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "value={" + this.value + "}";
    }
}
