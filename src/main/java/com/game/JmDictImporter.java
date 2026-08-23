package com.game;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class JmDictImporter {

    private static final int BATCH_SIZE = 1_000;

    private static final String DELETE_ALL_SQL = """
            DELETE FROM dictionary_words
            """;

    private static final String INSERT_SQL = """
            INSERT OR IGNORE INTO dictionary_words (
                jmdict_entry_id,
                surface,
                reading
            )
            VALUES (?, ?, ?)
            """;

    private static final String COUNT_SQL = """
            SELECT COUNT(*)
            FROM dictionary_words
            """;

    public long importFile(Path dictionaryPath)
            throws IOException,
            SQLException,
            XMLStreamException {

        if (!Files.isRegularFile(dictionaryPath)) {
            throw new IOException(
                    "JMdictファイルが見つかりません: "
                            + dictionaryPath.toAbsolutePath());
        }

        try (Connection connection = DatabaseManager.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();

            connection.setAutoCommit(false);

            try {
                deleteExistingWords(connection);

                long entryCount = importEntries(
                        connection,
                        dictionaryPath);

                long wordCount = countRegisteredWords(
                        connection);

                connection.commit();

                System.out.println(
                        "読み込んだエントリー数: "
                                + entryCount);

                return wordCount;

            } catch (
                    IOException
                    | SQLException
                    | XMLStreamException
                    | RuntimeException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(
                        originalAutoCommit);
            }
        }
    }

    private void deleteExistingWords(
            Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {

            statement.executeUpdate(
                    DELETE_ALL_SQL);
        }
    }

    private long importEntries(
            Connection connection,
            Path dictionaryPath) throws IOException,
            SQLException,
            XMLStreamException {

        XMLInputFactory xmlInputFactory = createXmlInputFactory();

        try (
                InputStream fileInputStream = Files.newInputStream(
                        dictionaryPath);
                InputStream gzipInputStream = new GZIPInputStream(
                        fileInputStream);
                InputStream bufferedInputStream = new BufferedInputStream(
                        gzipInputStream);
                PreparedStatement insertStatement = connection.prepareStatement(
                        INSERT_SQL)) {
            XMLStreamReader reader = xmlInputFactory
                    .createXMLStreamReader(
                            bufferedInputStream,
                            StandardCharsets.UTF_8
                                    .name());

            try {
                return readEntries(
                        reader,
                        insertStatement);
            } finally {
                reader.close();
            }
        }
    }

    private XMLInputFactory createXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        factory.setProperty(
                XMLInputFactory.SUPPORT_DTD,
                true);

        factory.setProperty(
                XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                false);

        factory.setProperty(
                XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                false);

        factory.setXMLResolver(
                (
                        publicId,
                        systemId,
                        baseUri,
                        namespace) -> new ByteArrayInputStream(
                                new byte[0]));

        return factory;
    }

    private long readEntries(
            XMLStreamReader reader,
            PreparedStatement insertStatement) throws XMLStreamException, SQLException {

        EntryData currentEntry = null;
        ReadingData currentReading = null;

        long entryCount = 0;
        int pendingInsertCount = 0;

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();

                switch (elementName) {
                    case "entry" ->
                        currentEntry = new EntryData();

                    case "ent_seq" -> {
                        if (currentEntry != null) {
                            currentEntry.entryId = Long.parseLong(
                                    reader
                                            .getElementText());
                        }
                    }

                    case "keb" -> {
                        if (currentEntry != null) {
                            currentEntry.spellings.add(
                                    reader
                                            .getElementText());
                        }
                    }

                    case "r_ele" ->
                        currentReading = new ReadingData();

                    case "reb" -> {
                        if (currentReading != null) {
                            currentReading.reading = reader
                                    .getElementText();
                        }
                    }

                    case "re_restr" -> {
                        if (currentReading != null) {
                            currentReading.restrictedSpellings
                                    .add(
                                            reader
                                                    .getElementText());
                        }
                    }

                    case "re_nokanji" -> {
                        if (currentReading != null) {
                            currentReading.noKanji = true;
                        }
                    }

                    default -> {
                    }
                }
            }

            if (event == XMLStreamConstants.END_ELEMENT) {
                String elementName = reader.getLocalName();

                if ("r_ele".equals(elementName)) {
                    if (currentEntry != null
                            && currentReading != null
                            && currentReading.reading != null) {
                        currentEntry.readings.add(
                                currentReading);
                    }

                    currentReading = null;
                }

                if ("entry".equals(elementName)) {
                    if (currentEntry != null) {
                        pendingInsertCount += queueEntry(
                                insertStatement,
                                currentEntry);

                        entryCount++;

                        if (pendingInsertCount >= BATCH_SIZE) {
                            executeBatch(
                                    insertStatement);

                            pendingInsertCount = 0;
                        }

                        if (entryCount
                                % 10_000 == 0) {
                            System.out.println(
                                    "読み込み中: "
                                            + entryCount
                                            + "件");
                        }
                    }

                    currentEntry = null;
                }
            }
        }

        if (pendingInsertCount > 0) {
            executeBatch(insertStatement);
        }

        return entryCount;
    }

    private int queueEntry(
            PreparedStatement insertStatement,
            EntryData entry) throws SQLException {

        if (entry.entryId <= 0) {
            return 0;
        }

        int queuedCount = 0;

        for (ReadingData readingData : entry.readings) {

            String originalReading = JapaneseTextNormalizer
                    .normalizeSurface(
                            readingData.reading);

            String normalizedReading = JapaneseTextNormalizer
                    .normalizeReading(
                            readingData.reading);

            if (originalReading.isEmpty()
                    || normalizedReading.isEmpty()) {
                continue;
            }

            queueInsert(
                    insertStatement,
                    entry.entryId,
                    originalReading,
                    normalizedReading);

            queuedCount++;

            if (!originalReading.equals(
                    normalizedReading)) {
                queueInsert(
                        insertStatement,
                        entry.entryId,
                        normalizedReading,
                        normalizedReading);

                queuedCount++;
            }

            if (readingData.noKanji) {
                continue;
            }

            List<String> availableSpellings;

            if (readingData.restrictedSpellings
                    .isEmpty()) {
                availableSpellings = entry.spellings;
            } else {
                availableSpellings = readingData.restrictedSpellings;
            }

            for (String spelling : availableSpellings) {

                String normalizedSpelling = JapaneseTextNormalizer
                        .normalizeSurface(
                                spelling);

                if (normalizedSpelling
                        .isEmpty()) {
                    continue;
                }

                queueInsert(
                        insertStatement,
                        entry.entryId,
                        normalizedSpelling,
                        normalizedReading);

                queuedCount++;
            }
        }

        return queuedCount;
    }

    private void queueInsert(
            PreparedStatement statement,
            long entryId,
            String surface,
            String reading) throws SQLException {

        statement.setLong(1, entryId);
        statement.setString(2, surface);
        statement.setString(3, reading);
        statement.addBatch();
    }

    private void executeBatch(
            PreparedStatement statement) throws SQLException {

        statement.executeBatch();
        statement.clearBatch();
    }

    private long countRegisteredWords(
            Connection connection) throws SQLException {

        try (
                var statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        COUNT_SQL)) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private static class EntryData {

        private long entryId;

        private final List<String> spellings = new ArrayList<>();

        private final List<ReadingData> readings = new ArrayList<>();
    }

    private static class ReadingData {

        private String reading;

        private boolean noKanji;

        private final List<String> restrictedSpellings = new ArrayList<>();
    }
}
