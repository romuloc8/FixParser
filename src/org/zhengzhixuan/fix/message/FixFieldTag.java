package org.zhengzhixuan.fix.message;

import org.zhengzhixuan.fix.resources.FixTagDictionary;

import java.io.IOException;

public class FixFieldTag {
    private final int tagNumber;
    private final String tagName;

    private FixFieldTag(int tagNumber, String tagName) {
        this.tagNumber = tagNumber;
        this.tagName = tagName;
    }

    public static FixFieldTag create(int tagNumber) {
        try {
            FixTagDictionary dictionary = FixTagDictionary.getDictionary();
            String tagName = dictionary.getTagNameByNumber(tagNumber);
            if (tagName == null) {
                throw new IllegalArgumentException(
                        String.format("Invalid FIX Message, invalid tag number %s seen in the message.", tagNumber));
            }
            return new FixFieldTag(tagNumber, tagName);
        } catch (IOException e) {
            throw new RuntimeException("Error occurs when creating FixFieldTag", e);
        }
    }

    public int getTagNumber() {
        return this.tagNumber;
    }

    public String getTagName() {
        return this.tagName;
    }

    @Override
    public String toString() {
        return "tag={" + this.tagNumber + ", " + this.tagName + "}";
    }
}
