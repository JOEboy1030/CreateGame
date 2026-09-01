package com.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineGameServer {

    private static final int PORT = 5000;

    public void start() {

        System.out.println("サーバーを起動します...");
        System.out.println("ポート: " + PORT);
        System.out.println("友達の接続を待っています...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Socket player1 = serverSocket.accept();

            System.out.println("プレイヤー1が接続しました。");

            Socket player2 = serverSocket.accept();

            System.out.println("プレイヤー2が接続しました。");

            System.out.println("2人そろいました！");
            System.out.println("ゲームを開始します。");

            OnlineGameSession session =
                    new OnlineGameSession(player1, player2);

            session.start();

        } catch (IOException e) {
            System.out.println("サーバーエラーが発生しました。");
            e.printStackTrace();
        }
    }
}
