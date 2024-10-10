package org.zhengzhixuan.fix.message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FixBody {
    private final List<FixField> fields;

    private FixBody(List<FixField> fields) {
        this.fields = fields;
    }

    public static FixBody create(Map<Integer, String> bodyTagToValues) {
        List<FixField> fields =
                bodyTagToValues.entrySet().stream()
                        .map(entry -> FixField.create(entry.getKey(), entry.getValue()))
                        .toList();
        return new FixBody(fields);
    }

    public List<FixField> getFields() {
        return this.fields;
    }

    @Override
    public String toString() {
        return "body={"
                + this.fields.stream().map(FixField::toString).collect(Collectors.joining(","))
                + "}";
    }
}
