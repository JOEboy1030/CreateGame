package com.game;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class GameController {

    private static final int MAX_WORD_GENERATION_ATTEMPTS = 100;

    private final Scanner scanner;
    private final WordGenerator wordGenerator;
    private final DictionaryRepository dictionaryRepository;
    private final SuffixVowelMatcher suffixVowelMatcher;
    private final RhymeJudge rhymeJudge;

    public GameController(Scanner scanner) {
        this.scanner = scanner;
        this.wordGenerator = new WordGenerator();
        this.dictionaryRepository = new DictionaryRepository();
        this.suffixVowelMatcher =
                new SuffixVowelMatcher(
                        new VowelSequenceConverter()
                );
        this.rhymeJudge = new RhymeJudge();
    }

    public void gameStart(int mode) {
        int selectedMode = mode;

        while (true) {
            if (selectedMode == 1) {
                System.out.println("1人でゲームを開始します。\n");
                gameModeSingle(selectedMode);
                selectedMode = selectGameMode();

            } else if (selectedMode == 2) {
                System.out.println("オンライン対戦モードを開始します。\n");

                OnlineGame onlineGame = new OnlineGame(scanner);
                onlineGame.start();
                return;

            } else {
                System.out.println("ゲームモードが不正です。");
                selectedMode = selectGameMode();
            }
        }
    }

    public void gameModeSingle(int mode) {
        while (true) {
            try {
                RhymeAnswer theme = generateRhymeAnswer();

                System.out.println("お題：「" + theme.word() + "」");
                System.out.println("読み：「" + theme.reading() + "」");
                System.out.print("回答する単語を入力してください。\n\n>");
                String playerWord = scanner.nextLine();

                Optional<RhymeAnswer> dictionaryPlayerAnswer =
                        findBestPlayerAnswer(theme, playerWord);

                RhymeAnswer playerAnswer = dictionaryPlayerAnswer.orElse(
                        new RhymeAnswer(playerWord, "")
                );

                boolean playerValid = dictionaryPlayerAnswer.isPresent();

                RhymeAnswer computerAnswer;

                do {
                    computerAnswer = generateRhymeAnswer();
                } while (JapaneseTextNormalizer.normalizeSurface(theme.word()).equals(
                        JapaneseTextNormalizer.normalizeSurface(computerAnswer.word())));

                RhymeResult result = rhymeJudge.judge(
                        theme, playerAnswer, playerValid, computerAnswer, true);

                System.out.println("\n=== 判定結果 ===");
                System.out.println("コンピューターの回答：「" + computerAnswer.word() + "」");
                System.out.println("コンピューターの回答の読み：「" + computerAnswer.reading() + "」");

                if (!result.player1Valid()) {
                    System.out.println(
                            "あなたの回答は無効です。"
                                    + "辞書に存在しない、韻判定に対応していない、"
                                    + "またはお題と同じ単語です。"
                    );
                } else {
                    System.out.println("あなたの回答：「" + playerAnswer.word() + "」");
                    System.out.println("あなたの回答の読み：「" + playerAnswer.reading() + "」");
                }

                System.out.println("あなたの母音一致数：" + result.player1MatchCount());
                System.out.println("コンピューターの母音一致数：" + result.player2MatchCount());
                System.out.println("あなたが受けるダメージ：" + result.damageToPlayer1());
                System.out.println("コンピューターが受けるダメージ：" + result.damageToPlayer2());

            } catch (SQLException e) {
                System.out.println("辞書データベースの参照に失敗しました。");
                e.printStackTrace();
                return;

            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                return;
            }

            int nextAction = selectNextAction();

            if (nextAction == 2) {
                return;
            }

            System.out.println();
        }
    }

    private Optional<RhymeAnswer> findBestPlayerAnswer(
            RhymeAnswer theme,
            String playerWord) throws SQLException {

        String normalizedPlayerWord =
                JapaneseTextNormalizer.normalizeSurface(playerWord);

        if (normalizedPlayerWord.isEmpty()) {
            return Optional.empty();
        }

        return dictionaryRepository.findReadings(normalizedPlayerWord).stream()
                .map(reading -> new RhymeAnswer(
                        normalizedPlayerWord,
                        reading
                ))
                .filter(answer ->
                        suffixVowelMatcher.isSupportedReading(answer.reading()))
                .max(Comparator.comparingInt(answer ->
                        suffixVowelMatcher.countMatches(
                                theme.reading(),
                                answer.reading()
                        )
                ));
    }

    private int selectNextAction() {
        while (true) {
            System.out.println("\n次のお題へ:1");
            System.out.println("モード選択へ:2");
            System.out.print(">");

            String input = scanner.nextLine();

            if ("1".equals(input)) {
                return 1;
            }

            if ("2".equals(input)) {
                return 2;
            }

            System.out.println("1または2を入力してください。");
        }
    }

    private int selectGameMode() {
        while (true) {
            System.out.println("\nゲームのモードを入力してください");
            System.out.println("1: 1人プレイ");
            System.out.println("2: オンライン対戦");
            System.out.print(">");

            String input = scanner.nextLine();

            if ("1".equals(input)) {
                return 1;
            }

            if ("2".equals(input)) {
                return 2;
            }

            System.out.println("1または2を入力してください。");
        }
    }

    private RhymeAnswer generateRhymeAnswer()
            throws SQLException {

        for (int attempt = 0;
                attempt < MAX_WORD_GENERATION_ATTEMPTS;
                attempt++) {

            String word = wordGenerator.getRandomWord();
            List<String> readings = dictionaryRepository.findReadings(word);

            for (String reading : readings) {
                if (suffixVowelMatcher.isSupportedReading(reading)) {
                    return new RhymeAnswer(word, reading);
                }
            }
        }

        throw new IllegalStateException(
                "韻判定に対応したお題を取得できませんでした。"
        );
    }
}
