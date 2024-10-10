package org.zhengzhixuan.fix.message;

import java.util.Map;

public class FixMessage {
    private final FixHeader header;
    private final FixBody body;

    private FixMessage(final FixHeader header, final FixBody body) {
        this.header = header;
        this.body = body;
    }

    public static FixMessage create(Map<Integer, String> headerTagToValues, Map<Integer, String> bodyTagToValues) {
        return new FixMessage(FixHeader.create(headerTagToValues), FixBody.create(bodyTagToValues));
    }

    public FixHeader getHeader() {
        return this.header;
    }

    public FixBody getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "message={" + this.header.toString() + "," + this.body.toString() + "}";
    }
}
