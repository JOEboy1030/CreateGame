package com.game;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class JmDictImportMain {

    private static final Path DEFAULT_DICTIONARY_PATH = Path.of(
            "data",
            "JMdict_e.gz");

    private JmDictImportMain() {
    }

    public static void main(String[] args) {
        Path dictionaryPath = getDictionaryPath(args);

        if (!Files.isRegularFile(dictionaryPath)) {
            System.out.println(
                    "JMdictファイルが見つかりません。");

            System.out.println(
                    "確認する場所: "
                            + dictionaryPath
                                    .toAbsolutePath());

            return;
        }

        try {
            System.out.println(
                    "SQLiteを初期化しています……");

            DictionaryInitializer.initialize();

            System.out.println(
                    "JMdictの取り込みを開始します。");

            System.out.println(
                    "読み込み元: "
                            + dictionaryPath
                                    .toAbsolutePath());

            JmDictImporter importer = new JmDictImporter();

            long registeredWordCount = importer.importFile(
                    dictionaryPath);

            System.out.println(
                    "JMdictの取り込みが完了しました。");

            System.out.println(
                    "SQLiteファイル: "
                            + DatabaseManager
                                    .getDatabasePath()
                                    .toAbsolutePath());

            System.out.println(
                    "登録された表記と読み: "
                            + registeredWordCount
                            + "件");

        } catch (IOException e) {
            System.out.println(
                    "ファイルの読み込みに失敗しました。");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println(
                    "SQLiteへの登録に失敗しました。");
            e.printStackTrace();

        } catch (XMLStreamException e) {
            System.out.println(
                    "JMdict XMLの解析に失敗しました。");
            e.printStackTrace();
        }
    }

    private static Path getDictionaryPath(
            String[] args) {
        if (args.length > 0) {
            return Path.of(args[0]);
        }

        return DEFAULT_DICTIONARY_PATH;
    }
}