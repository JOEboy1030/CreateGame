package com.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineGameSession {


    private final Socket player2;

    private final WordGenerator wordGenerator = new WordGenerator();

    public OnlineGameSession(
            int hand,
            Socket player2
    ) {
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

            out1.println("=== ゲーム開始 ===");
            out1.println("お題：" + word);


            out2.println("=== ゲーム開始 ===");
            out2.println("お題：" + word);

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
}
