//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.zhengzhixuan.fix.message.FixMessage;
import org.zhengzhixuan.fix.parser.FixParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        FixParser parser = FixParser.create();
        Path fixMessagesPath = Paths.get(args[0]);
        try {
            BufferedReader reader = Files.newBufferedReader(fixMessagesPath, StandardCharsets.US_ASCII);
            reader.lines()
                    .map(message -> parser.parse(message.getBytes(StandardCharsets.US_ASCII)))
                    .forEach(System.out::println);
        } catch (IOException e) {
            throw new IOException("Error occurs when reading FIX message file", e);
        }
    }
}