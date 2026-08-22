package com.game;

import java.util.random.*;

public class GameController {

    private int hand;
    private RandomGenerator random;

    public GameController(int hand) {
        this.hand = hand;
        this.random = RandomGenerator.getDefault();

    }

    public void gameStart() {
        System.out.println("GameStart(手札:" + hand + ")");

    }

}
