package com.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DictionaryRepository {

    private static final String FIND_SQL = """
            SELECT surface, reading
            FROM dictionary_words
            WHERE surface = ?
              AND reading = ?
            LIMIT 1
            """;

    private static final String FIND_READINGS_SQL = """
            SELECT DISTINCT reading
            FROM dictionary_words
            WHERE surface = ?
            ORDER BY reading
            """;

    public Optional<DictionaryWord> find(String surface, String reading) throws SQLException {

        String normalizedSurface = JapaneseTextNormalizer.normalizeSurface(surface);

        String normalizedReading = JapaneseTextNormalizer.normalizeReading(reading);

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_SQL)) {
            statement.setString(1, normalizedSurface);

            statement.setString(2, normalizedReading);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(
                        new DictionaryWord(
                                resultSet.getString("surface"),
                                resultSet.getString("reading")));
            }
        }
    }

    public List<String> findReadings(String surface) throws SQLException {

        String normalizedSurface = JapaneseTextNormalizer.normalizeSurface(surface);

        List<String> readings = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_READINGS_SQL)) {
            statement.setString(1, normalizedSurface);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    readings.add(resultSet.getString("reading"));
                }
            }
        }

        return readings;
    }
}
