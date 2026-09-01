package com.game;

import java.sql.SQLException;
import java.util.Optional;

public class WordGenerator {

    private final DictionaryRepository repository;

    public WordGenerator() {
        this.repository = new DictionaryRepository();
    }

    public String getRandomWord() {
        try {
            Optional<DictionaryWord> dictionaryWord = repository.findRandom();

            if (dictionaryWord.isEmpty()) {
                throw new RuntimeException(
                        "辞書データベースに単語が登録されていません。");
            }

            return dictionaryWord.get().surface();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "辞書データベースの参照に失敗しました。",
                    e);
        }
    }
}
