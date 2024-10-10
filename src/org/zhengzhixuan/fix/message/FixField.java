package org.zhengzhixuan.fix.message;

public class FixField {
    private final FixFieldTag tag;
    private final FixFieldValue value;

    private FixField(FixFieldTag tag, FixFieldValue value) {
        this.tag = tag;
        this.value = value;
    }

    public static FixField create(int tagNumber, String value) {
        return new FixField(FixFieldTag.create(tagNumber), FixFieldValue.create(value));
    }

    public FixFieldTag getTag() {
        return this.tag;
    }

    public FixFieldValue getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "field={" + this.tag.toString() + "," + this.value.toString() + "}";
    }
}
