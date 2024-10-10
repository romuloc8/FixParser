package org.zhengzhixuan.fix.parser;

import org.zhengzhixuan.fix.message.FixMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FixParser {
    // Defines the minimum number of fields in a message. 5 for FIX 4.4, of which 3 fields is for header, 1 field for
    // body and 1 field for trailer.
    private static final int MIN_NUM_OF_FIELDS = 5;
    private static final int NUM_OF_FIELDS_IN_HEADER = 3;

    private FixParser() {
    }

    public static FixMessage parse(byte[] bytes) {
        checkChecksum(bytes);
        checkBodyLength(bytes);
        List<String> fieldStrings = divideIntoFields(bytes);
        if (fieldStrings.size() < MIN_NUM_OF_FIELDS) {
            throw new IllegalArgumentException("Invalid FIX message, no enough fields exist in the message.");
        }
        Map<Integer, String> headerTagToValues =
                fieldStrings.subList(0, NUM_OF_FIELDS_IN_HEADER).stream()
                        .map(FixParser::parseFieldString)
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        Map<Integer, String> bodyTagToValues =
                fieldStrings.subList(NUM_OF_FIELDS_IN_HEADER, fieldStrings.size() - 1).stream()
                        .map(FixParser::parseFieldString)
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return FixMessage.create(headerTagToValues, bodyTagToValues);
    }

    private static void checkChecksum(byte[] bytes) {
        String message = new String(bytes, StandardCharsets.US_ASCII);
        Pattern checkSumPattern = Pattern.compile("\00110=[0-9]+");
        Matcher checkSumMatcher = checkSumPattern.matcher(message);
        if (!checkSumMatcher.find()) {
            throw new IllegalArgumentException("Invalid FIX message, no Checksum tag found.");
        } else {
            int checksumIndex = checkSumMatcher.start();
            int expectedChecksum = Integer.parseInt(message.substring(checksumIndex + 4, checkSumMatcher.end()));
            int actualChecksum = 0;
            for (int i = 0; i <= checksumIndex; i++) {
                actualChecksum += bytes[i];
            }
            actualChecksum %= 256;
            if (actualChecksum != expectedChecksum) {
                throw new IllegalArgumentException(
                        "Invalid FIX message, received checksum does not match expected checksum.");
            }
        }
    }

    private static void checkBodyLength(byte[] bytes) {
        String message = new String(bytes, StandardCharsets.US_ASCII);
        Pattern checkSumPattern = Pattern.compile("\00110=");
        Pattern bodyLengthPattern = Pattern.compile("\0019=[0-9]+");
        Matcher checkSumMatcher = checkSumPattern.matcher(message);
        Matcher bodyLengthMatcher = bodyLengthPattern.matcher(message);
        if (!checkSumMatcher.find()) {
            throw new IllegalArgumentException("Invalid FIX message, no Checksum tag found.");
        } else if (!bodyLengthMatcher.find()) {
            throw new IllegalArgumentException("Invalid FIX message, no BodyLength tag found.");
        } else {
            int expectedBodyLength =
                    Integer.parseInt(message.substring(bodyLengthMatcher.start() + 3, bodyLengthMatcher.end()));
            int actualBodyLength = checkSumMatcher.start() - bodyLengthMatcher.end();
            if (actualBodyLength != expectedBodyLength) {
                throw new IllegalArgumentException(
                        "Invalid FIX message, received body length does not match expected body length.");
            }
        }
    }

    private static List<String> divideIntoFields(byte[] bytes) {
        return Arrays.asList(new String(bytes, StandardCharsets.US_ASCII).split("\001"));
    }

    private static Entry<Integer, String> parseFieldString(String field) {
        String[] tagAndValue = field.split("=");
        if (tagAndValue.length != 2) {
            throw new IllegalArgumentException(
                    String.format("Invalid FIX message, invalid field %s seen in the message.", field));
        } else {
            try {
                return Map.entry(Integer.parseInt(tagAndValue[0]), tagAndValue[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("Invalid FIX message, non-numeric tag %s seen in the message.", tagAndValue[0]));
            }
        }
    }
}
