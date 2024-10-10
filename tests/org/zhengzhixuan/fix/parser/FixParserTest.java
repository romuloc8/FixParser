package org.zhengzhixuan.fix.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.zhengzhixuan.fix.message.FixMessage;
import java.nio.charset.StandardCharsets;

class FixParserTest {
    private final FixParser parser = FixParser.create();

    @Test
    public void parse_success() {
        byte[] bytes =
                ("8=FIX.4.4\0019=65\00135=A"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=064\001")
                        .getBytes(StandardCharsets.US_ASCII);
        String expectedString = "message={header={field={tag={8, BeginString},value={FIX.4.4}},"
                + "field={tag={9, BodyLength},value={65}},field={tag={35, MsgType},value={A}}},"
                + "body={field={tag={49, SenderCompID},value={SERVER}},field={tag={34, MsgSeqNum},"
                + "value={177}},field={tag={98, EncryptMethod},value={0}},field={tag={52, SendingTime}," +
                "value={20090107-18:15:16}},field={tag={56, TargetCompID},value={CLIENT}},"
                + "field={tag={108, HeartBtInt},value={30}}}}";
        FixMessage message = parser.parse(bytes);

        assertEquals(message.getHeader().getBeginString().getTag().getTagNumber(), 8);
        assertEquals(message.getHeader().getBeginString().getTag().getTagName(), "BeginString");
        assertEquals(message.getHeader().getBeginString().getValue().getValue(), "FIX.4.4");
        assertEquals(message.getHeader().getBodyLength().getTag().getTagNumber(), 9);
        assertEquals(message.getHeader().getBodyLength().getTag().getTagName(), "BodyLength");
        assertEquals(message.getHeader().getBodyLength().getValue().getValue(), "65");
        assertEquals(message.getHeader().getMsgType().getTag().getTagNumber(), 35);
        assertEquals(message.getHeader().getMsgType().getTag().getTagName(), "MsgType");
        assertEquals(message.getHeader().getMsgType().getValue().getValue(), "A");
        assertEquals(message.getBody().getFields().size(), 6);
        assertEquals(message.getBody().getFields().getFirst().getTag().getTagNumber(), 49);
        assertEquals(message.getBody().getFields().getFirst().getTag().getTagName(), "SenderCompID");
        assertEquals(message.getBody().getFields().getFirst().getValue().getValue(), "SERVER");
        assertEquals(message.getBody().getFields().get(1).getTag().getTagNumber(), 34);
        assertEquals(message.getBody().getFields().get(1).getTag().getTagName(), "MsgSeqNum");
        assertEquals(message.getBody().getFields().get(1).getValue().getValue(), "177");
        assertEquals(message.getBody().getFields().get(2).getTag().getTagNumber(), 98);
        assertEquals(message.getBody().getFields().get(2).getTag().getTagName(), "EncryptMethod");
        assertEquals(message.getBody().getFields().get(2).getValue().getValue(), "0");
        assertEquals(message.getBody().getFields().get(3).getTag().getTagNumber(), 52);
        assertEquals(message.getBody().getFields().get(3).getTag().getTagName(), "SendingTime");
        assertEquals(message.getBody().getFields().get(3).getValue().getValue(), "20090107-18:15:16");
        assertEquals(message.getBody().getFields().get(4).getTag().getTagNumber(), 56);
        assertEquals(message.getBody().getFields().get(4).getTag().getTagName(), "TargetCompID");
        assertEquals(message.getBody().getFields().get(4).getValue().getValue(), "CLIENT");
        assertEquals(message.getBody().getFields().getLast().getTag().getTagNumber(), 108);
        assertEquals(message.getBody().getFields().getLast().getTag().getTagName(), "HeartBtInt");
        assertEquals(message.getBody().getFields().getLast().getValue().getValue(), "30");
        assertEquals(message.toString(), expectedString);


    }

    @Test
    public void parse_noChecksum() {
        byte[] bytes =
                ("8=FIX.4.4\0019=65\00135=A"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(exception.getMessage(), "Invalid FIX message, no Checksum tag found.");
    }

    @Test
    void parse_invalidChecksum() {
        byte[] bytes =
                ("8=FIX.4.4\0019=65\00135=A"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=065\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(),
                "Invalid FIX message, received checksum does not match expected checksum.");
    }

    @Test
    public void parse_noBodyLength() {
        byte[] bytes =
                ("8=FIX.4.4\00135=A"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=094\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX message, no BodyLength tag found.");
    }

    @Test
    public void parse_invalidBodyLength() {
        byte[] bytes =
                ("8=FIX.4.4\0019=66\00135=A"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=065\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(),
                "Invalid FIX message, received body length does not match expected body length.");
    }

    @Test
    public void parse_noEnoughFields() {
        byte[] bytes =
                ("8=FIX.4.4\0019=5\00135=A\00110=180\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX message, no enough fields exist in the message.");
    }

    @Test
    public void parse_noSufficientHeaderField() {
        byte[] bytes =
                ("8=FIX.4.4\0019=60"
                        + "\00149=SERVER\00156=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=084\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX message, header-required tags missing in the message.");
    }

    @Test
    public void parse_invalidField() {
        byte[] bytes =
                ("8=FIX.4.4\0019=63\00135=A"
                        + "\00149=SERVER\001INVALID\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=222\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX message, invalid field INVALID seen in the message.");
    }

    @Test
    public void parse_nonNumericTag() {
        byte[] bytes =
                ("8=FIX.4.4\0019=65\00135=A"
                        + "\00149=SERVER\001AA=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=087\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX message, non-numeric tag AA seen in the message.");
    }

    @Test
    public void parse_invalidTag() {
        byte[] bytes =
                ("8=FIX.4.4\0019=68\00135=A"
                        + "\00149=SERVER\00110000=CLIENT\00134=177"
                        + "\00152=20090107-18:15:16\00198=0"
                        + "\001108=30\00110=201\001")
                        .getBytes(StandardCharsets.US_ASCII);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> parser.parse(bytes));
        assertEquals(
                exception.getMessage(), "Invalid FIX Message, invalid tag number 10000 seen in the message.");
    }
}