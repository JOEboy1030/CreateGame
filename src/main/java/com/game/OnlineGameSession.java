package com.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineGameSession {

    private final Socket player1;
    private final Socket player2;

    private final WordGenerator wordGenerator = new WordGenerator();

    public OnlineGameSession(
            Socket player1,
            Socket player2
    ) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void start() {
        try {
            PrintWriter out1 =
                    new PrintWriter(
                            player1.getOutputStream(),
                            true
                    );

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
            e.printStackTrace();
        }
    }
}
