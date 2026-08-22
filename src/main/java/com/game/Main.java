package com.game;

public class Main {
    private static final int DEFAULTHAND = 10;

    public static void main(String[] args) {
        int hand = DEFAULTHAND;

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
        GameController gameController = new GameController(hand);
        gameController.gameStart();

    }
}
