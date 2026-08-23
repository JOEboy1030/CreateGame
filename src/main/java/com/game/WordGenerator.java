package com.game;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.zip.GZIPInputStream;

public class WordGenerator {

    private static final String WORD_FILE = "/home/joe1030/CreateGame/data/JMdict_e.gz";

    private final RandomGenerator random;

    public WordGenerator() {
        random = RandomGenerator.getDefault();
    }

    public String getRandomWord() {

        List<String> words = new ArrayList<>();

        try (
                InputStream inputStream = Files.newInputStream(Path.of(WORD_FILE));

                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                gzipInputStream,
                                StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // とりあえず単語っぽいものを探す
                if (line.contains("<keb>")) {

                    int start = line.indexOf("<keb>") + 5;
                    int end = line.indexOf("</keb>");

                    if (start >= 5 && end > start) {
                        String word = line.substring(start, end);
                        words.add(word);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("辞書ファイルの読み込みに失敗しました", e);
        }

        if (words.isEmpty()) {
            throw new RuntimeException("単語が見つかりませんでした");
        }

        return words.get(random.nextInt(words.size()));
    }
}
