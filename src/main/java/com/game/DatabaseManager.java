package com.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Path DATABASE_PATH = Path.of("data", "jmdict.db");

    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;

    private DatabaseManager() {
    }

    public static Connection getConnection()
            throws SQLException {

        createDataDirectory();

        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try {
            configureConnection(connection);
            return connection;

        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    private static void createDataDirectory()
            throws SQLException {
        try {
            Files.createDirectories(
                    DATABASE_PATH.getParent()
            );
        } catch (IOException e) {
            throw new SQLException("dataフォルダを作成できませんでした。", e
            );
        }
    }

    private static void configureConnection(
            Connection connection
    ) throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute("PRAGMA busy_timeout = 5000");
        }
    }

    public static Path getDatabasePath() {
        return DATABASE_PATH;
    }
}
