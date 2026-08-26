package com.game;

import java.util.Scanner;

public class OnlineGame {

    private final int hand;
    private final Scanner scanner = new Scanner(System.in);

    public OnlineGame(int hand) {
        this.hand = hand;
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

            OnlineGameServer server = new OnlineGameServer(hand);
            server.start();

        } else if (mode == 2) {

            System.out.print("サーバーのIPアドレスを入力してください: ");
            String host = scanner.nextLine();

            OnlineGameClient client = new OnlineGameClient(host);
            client.start();

        } else {
            System.out.println("入力が不正です。");
        }
    }
}