package com.game;

import java.sql.SQLException;

public class WordValidator {

    private final DictionaryRepository repository;

    public WordValidator(
            DictionaryRepository repository) {
        this.repository = repository;
    }

    public boolean isValid(String surface,String reading) throws SQLException {

        String normalizedSurface = JapaneseTextNormalizer.normalizeSurface(surface);

        String normalizedReading = JapaneseTextNormalizer.normalizeReading(reading);

        if (normalizedSurface.isEmpty() || normalizedReading.isEmpty()) {
            return false;
        }

        return repository.find(normalizedSurface,normalizedReading).isPresent();
    }
}
