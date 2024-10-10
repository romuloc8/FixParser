//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.zhengzhixuan.fix.message.FixMessage;
import org.zhengzhixuan.fix.parser.FixParser;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        byte[] bytes = "8=FIX.4.2\0019=65\00135=A\00149=SERVER\00156=CLIENT\00134=177\00152=20090107-18:15:16\00198=0\001108=30\00110=062\001".getBytes(StandardCharsets.US_ASCII);
        FixMessage message = FixParser.parse(bytes);
    }
}