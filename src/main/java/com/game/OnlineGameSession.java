package com.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Random;

public class OnlineGameSession {

    private final int hand;

    // 友達（プレイヤー2）のSocketだけ持つ
    private final Socket player2;

    private final Random random = new SecureRandom();

    private final WordGenerator wordGenerator = new WordGenerator();

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

    public OnlineGameSession(
            int hand,
            Socket player2
    ) {
        this.hand = hand;
        this.player2 = player2;
    }

    public void start() {

        try {

            PrintWriter out2 =
                    new PrintWriter(
                            player2.getOutputStream(),
                            true
                    );

            String word = wordGenerator.getRandomWord();

            String cards = createHand();

            // =========================
            // プレイヤー1（自分）
            // =========================

            System.out.println("=== ゲーム開始 ===");
            System.out.println("お題：" + word);
            System.out.println("あなたの手札：" + cards);

            // =========================
            // プレイヤー2（友達）
            // =========================

            out2.println("=== ゲーム開始 ===");
            out2.println("お題：" + word);
            out2.println("あなたの手札：" + cards);

        } catch (IOException e) {

            System.out.println("通信エラーが発生しました。");
            e.printStackTrace();

        } finally {

            try {
                player2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String createHand() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hand; i++) {

            int index =
                    random.nextInt(HIRAGANA.length);

            result.append(HIRAGANA[index]);

            if (i < hand - 1) {
                result.append(", ");
            }
        }

        return result.toString();
    }
}