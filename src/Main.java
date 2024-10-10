import org.zhengzhixuan.fix.message.FixMessage;
import org.zhengzhixuan.fix.parser.FixParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Main class for testing the parser.
 * With the test set located in performance folder, which has 80 messages.
 * It takes 0.024s to process these messages, with average 0.0003s per message.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String path = args.length > 0 ? args[0] : ".\\src\\org\\zhengzhixuan\\fix\\performance\\TESTSET.txt";
        FixParser parser = FixParser.create();
        Path fixMessagesPath = Paths.get(path);
        try {
            BufferedReader reader = Files.newBufferedReader(fixMessagesPath, StandardCharsets.US_ASCII);
            List<String> messages =  reader.lines().toList();
            Instant start = Instant.now();
            List<FixMessage> fixMessages =
                    messages.stream()
                            .map(message -> parser.parse(message.getBytes(StandardCharsets.US_ASCII))).toList();
            Instant end = Instant.now();
            fixMessages.forEach(System.out::println);
            System.out.println(Duration.between(start, end));
        } catch (IOException e) {
            throw new IOException("Error occurs when reading FIX message file.", e);
        }
    }
}