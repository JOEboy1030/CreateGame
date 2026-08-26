package com.game;

import java.security.SecureRandom;
import java.util.Random;

public class GameController {

    private int hand;
    private Random random;
    private WordGenerator wordGenerator;
    // 小さい文字や濁点追加するか検討
    private static final String[] HIRAGANA = {
            "あ", "い", "う", "え", "お",
            "か", "き", "く", "け", "こ",
            "さ", "し", "す", "せ", "そ",
            "た", "ち", "つ", "て", "と",
            "な", "に", "ぬ", "ね", "の",
            "は", "ひ", "ふ", "へ", "ほ",
            "ま", "み", "む", "め", "も",
            "や", "ゆ", "よ",
            "ら", "り", "る", "れ", "ろ",
            "わ", "を", "ん"
    };

    public GameController(int hand) {
        this.hand = hand;
        this.random = new SecureRandom();
        this.wordGenerator = new WordGenerator();

    }

    public void gameStart(int mode) {

        if (mode == 1) {
            System.out.println("1人でゲームを開始します。\n");
            gameModeSingle(mode);

        } else if (mode == 2) {
            System.out.println("オンライン対戦モードを開始します。\n");

            OnlineGame onlineGame = new OnlineGame(hand);
            onlineGame.start();

        } else {
            System.out.println("ゲームモードが不正です。");
        }

    }

    public void gameModeSingle(int mode) {
        System.out.println("手札を配ります。\n");
        for (int i = 0; i < hand; i++) {
            int index = random.nextInt(HIRAGANA.length);
            System.out.print(HIRAGANA[index] + ",");
        }
        String word = wordGenerator.getRandomWord();

        System.out.println("\n\nお題：「" + word + "」です。手札から韻を考えてください。");

    }

}
