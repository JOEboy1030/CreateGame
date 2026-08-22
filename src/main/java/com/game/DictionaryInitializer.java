package com.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DictionaryInitializer {

    private static final String SCHEMA_FILE =
            "/schema.sql";

    private DictionaryInitializer() {
    }

    public static void initialize()
            throws IOException, SQLException {

        String schema = readSchema();

        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute("PRAGMA journal_mode = WAL");

            executeSchema(statement, schema);
        }
    }

    private static String readSchema()
            throws IOException {

        try (InputStream inputStream = DictionaryInitializer.class.getResourceAsStream(SCHEMA_FILE)) {

            if (inputStream == null) {
                throw new IOException("schema.sqlが見つかりません。");
            }

            return new String(inputStream.readAllBytes(),StandardCharsets.UTF_8
            );
        }
    }

    private static void executeSchema(
            Statement statement,
            String schema
    ) throws SQLException {

        String[] sqlStatements = schema.split(";");

        for (String sql : sqlStatements) {
            String trimmedSql = sql.trim();

            if (trimmedSql.isEmpty()) {
                continue;
            }

            statement.execute(trimmedSql);
        }
    }
}
