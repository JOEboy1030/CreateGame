package com.game;

import java.util.Scanner;

public class Main {
    private static final int DEFAULTHAND = 30;

    public static void main(String[] args) {
        int hand = DEFAULTHAND;
        // ゲームモードを数値で管理（１：一人用、２：対戦用）
        int mode;
        Scanner sc = new Scanner(System.in);

        if (args.length > 0) {
            try {
                int input = Integer.parseInt(args[0]);
                if (input <= 0) {
                    throw new Exception();
                }
                hand = input;
            } catch (Exception e) {
                System.out.println("入力値が不正なため" + DEFAULTHAND + "でゲームを開始します。");
            }

        }

        System.out.println("ゲームを" + hand + "で開催します\n");
        System.out.print("ゲームのモードを入力してください\n\n>");
        mode = sc.nextInt();
        GameController gameController = new GameController(hand);
        gameController.gameStart(mode);

    }
}
