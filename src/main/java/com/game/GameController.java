package com.game;

import java.security.SecureRandom;
import java.util.Random;

public class GameController {

    private int hand;
    private Random random;
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

    }

    public void gameStart(int mode) {

        if (mode == 1) {
            System.out.println("1人でゲームを開始します。\n");
            gameMode(mode);
        } else {
            System.out.println("でゲームを開始します。\n");
            System.out.println("まだ開発中です\n");
        }

    }

    public void gameMode(int mode) {
        System.out.println("手札を配ります。\n");
        for (int i = 0; i < hand; i++) {
            int index = random.nextInt(HIRAGANA.length);
            System.out.print(HIRAGANA[index] + ",");
        }

    }

}
