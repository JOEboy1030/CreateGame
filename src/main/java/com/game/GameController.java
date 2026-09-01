package com.game;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GameController {

    private static final int MAX_WORD_GENERATION_ATTEMPTS = 100;

    private Scanner scanner;
    private WordGenerator wordGenerator;
    private DictionaryRepository dictionaryRepository;
    private WordValidator wordValidator;
    private SuffixVowelMatcher suffixVowelMatcher;
    private RhymeJudge rhymeJudge;

    public GameController(Scanner scanner) {
    this.scanner = scanner;
    this.wordGenerator = new WordGenerator();
    this.dictionaryRepository = new DictionaryRepository();
    this.wordValidator = new WordValidator(dictionaryRepository);
    this.suffixVowelMatcher =
            new SuffixVowelMatcher(
                    new VowelSequenceConverter()
            );
    this.rhymeJudge = new RhymeJudge();
}

    public void gameStart(int mode) {

        if (mode == 1) {
            System.out.println("1人でゲームを開始します。\n");
            gameModeSingle(mode);

        } else if (mode == 2) {
            System.out.println("オンライン対戦モードを開始します。\n");

            OnlineGame onlineGame =
                    new OnlineGame(scanner);

            onlineGame.start();

        } else {
            System.out.println("ゲームモードが不正です。");
        }
    }

    public void gameModeSingle(int mode) {

        try {
            RhymeAnswer theme =
                    generateRhymeAnswer();

            System.out.println(
                    "お題：「" + theme.word() + "」"
            );

            System.out.println(
                    "読み：「" + theme.reading() + "」"
            );

            System.out.print(
                    "回答する単語を入力してください。\n\n>"
            );

            String playerWord =
                    scanner.nextLine();

            System.out.print(
                    "回答する単語の読みを入力してください。\n\n>"
            );

            String playerReading =
                    scanner.nextLine();

            RhymeAnswer playerAnswer =
                    new RhymeAnswer(
                            playerWord,
                            playerReading
                    );

            boolean playerValid =
                    wordValidator.isValid(
                            playerWord,
                            playerReading
                    );

            RhymeAnswer computerAnswer;

            do {
                computerAnswer =
                        generateRhymeAnswer();

            } while (
                    JapaneseTextNormalizer
                            .normalizeSurface(
                                    theme.word()
                            )
                            .equals(
                                    JapaneseTextNormalizer
                                            .normalizeSurface(
                                                    computerAnswer.word()
                                            )
                            )
            );

            RhymeResult result =
                    rhymeJudge.judge(
                            theme,
                            playerAnswer,
                            playerValid,
                            computerAnswer,
                            true
                    );

            System.out.println(
                    "\nコンピューターの回答：「"
                            + computerAnswer.word()
                            + "」"
            );

            System.out.println(
                    "コンピューターの回答の読み：「"
                            + computerAnswer.reading()
                            + "」"
            );

            if (!result.player1Valid()) {
                System.out.println(
                        "あなたの回答は無効です。"
                );
            }

            System.out.println(
                    "あなたの母音一致数："
                            + result.player1MatchCount()
            );

            System.out.println(
                    "コンピューターの母音一致数："
                            + result.player2MatchCount()
            );

            System.out.println(
                    "あなたが受けるダメージ："
                            + result.damageToPlayer1()
            );

            System.out.println(
                    "コンピューターが受けるダメージ："
                            + result.damageToPlayer2()
            );

        } catch (SQLException e) {
            System.out.println(
                    "辞書データベースの参照に失敗しました。"
            );

            e.printStackTrace();

        } catch (IllegalStateException e) {
            System.out.println(
                    e.getMessage()
            );
        }
    }

    private RhymeAnswer generateRhymeAnswer()
            throws SQLException {

        for (
                int attempt = 0;
                attempt < MAX_WORD_GENERATION_ATTEMPTS;
                attempt++
        ) {
            String word =
                    wordGenerator.getRandomWord();

            List<String> readings =
                    dictionaryRepository.findReadings(
                            word
                    );

            for (String reading : readings) {

                if (
                        suffixVowelMatcher
                                .isSupportedReading(
                                        reading
                                )
                ) {
                    return new RhymeAnswer(
                            word,
                            reading
                    );
                }
            }
        }

        throw new IllegalStateException(
                "韻判定に対応したお題を取得できませんでした。"
        );
    }
}