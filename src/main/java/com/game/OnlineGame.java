package com.game;

import java.util.Scanner;

public class OnlineGame {

    private final Scanner scanner;

    public OnlineGame(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {

        System.out.println("=== オンライン対戦 ===");
        System.out.println("1: 部屋を作る");
        System.out.println("2: 部屋に参加する");
        System.out.print("> ");

        int mode;

        try {
            mode = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("入力が不正です。");
            return;
        }

        if (mode == 1) {

            OnlineGameServer server = new OnlineGameServer();
            server.start();

        } else if (mode == 2) {

            System.out.print("接続先ホスト：");
            String host = scanner.nextLine();
            System.out.print("接続先ポート：");

            int port;

            try {
                port = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ポート番号が不正です。");
                return;
            }

            OnlineGameClient client = new OnlineGameClient(host, port);
            client.start();

        } else {
            System.out.println("入力が不正です。");
        }
    }
}
