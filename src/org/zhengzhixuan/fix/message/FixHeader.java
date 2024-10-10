package org.zhengzhixuan.fix.message;

import java.util.Map;
import java.util.Set;

public final class FixHeader {
    private static final int BEGIN_STRING_TAG_NUMBER = 8;
    private static final int BODY_LENGTH_TAG_NUMBER = 9;
    private static final int MSG_TYPE_TAG_NUMBER = 35;
    private static final Set<Integer> REQUIRED_TAGS =
            Set.of(BEGIN_STRING_TAG_NUMBER, BODY_LENGTH_TAG_NUMBER, MSG_TYPE_TAG_NUMBER);

    // Based on the FIX 4.4 specification, FIX header will only contain these three fields.
    private final FixField beginString;
    private final FixField bodyLength;
    private final FixField MsgType;

    private FixHeader(FixField beginString, FixField bodyLength, FixField MsgType) {
        this.beginString = beginString;
        this.bodyLength = bodyLength;
        this.MsgType = MsgType;
    }

    public static FixHeader create(Map<Integer, String> headerTagToValues) {
        if (!headerTagToValues.keySet().equals(REQUIRED_TAGS)) {
            throw new IllegalArgumentException("Invalid FIX message, header-required tags missing in the message.");
        } else {
            return new FixHeader(
                    FixField.create(BEGIN_STRING_TAG_NUMBER, headerTagToValues.get(BEGIN_STRING_TAG_NUMBER)),
                    FixField.create(BODY_LENGTH_TAG_NUMBER, headerTagToValues.get(BODY_LENGTH_TAG_NUMBER)),
                    FixField.create(MSG_TYPE_TAG_NUMBER, headerTagToValues.get(MSG_TYPE_TAG_NUMBER)));
        }
    }

    public FixField getBeginString() {
        return this.beginString;
    }

    public FixField getBodyLength() {
        return this.bodyLength;
    }

    public FixField getMsgType() {
        return this.MsgType;
    }
}
