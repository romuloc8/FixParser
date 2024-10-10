package org.zhengzhixuan.fix.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class FixTagDictionary {
    private static FixTagDictionary INSTANCE;
    private final Map<Integer, String> tagDefinitions;

    private FixTagDictionary() throws IOException {
        Path csvPath = Paths.get(".\\src\\org\\zhengzhixuan\\fix\\resources\\FIX44TAG.csv");

        try {
            BufferedReader reader = Files.newBufferedReader(csvPath);
            tagDefinitions =
                    reader.lines()
                            .map(line -> line.split(","))
                            .collect(Collectors.toMap(array -> Integer.parseInt(array[0]), array -> array[1]));
        } catch (IOException e) {
            throw new IOException("Error occurs when reading FIX tag dictionary", e);
        }
    }

    public static FixTagDictionary getDictionary() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new FixTagDictionary();
        }
        return INSTANCE;
    }

    public String getTagNameByNumber(int number) {
        return this.tagDefinitions.get(number);
    }
}
