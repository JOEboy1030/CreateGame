package com.game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ゲームモードを数値で管理（１：一人用、２：対戦用）
        int mode;
        Scanner sc = new Scanner(System.in);

        System.out.print("ゲームのモードを入力してください\n\n>");

        try {
            mode = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ゲームモードは1または2を入力してください。");
            sc.close();
            return;
        }

        GameController gameController = new GameController(sc);
        gameController.gameStart(mode);

        sc.close();

    }
}
