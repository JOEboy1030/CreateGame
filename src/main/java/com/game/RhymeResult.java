package com.game;

public record RhymeResult(boolean player1Valid,boolean player2Valid,int player1MatchCount,int player2MatchCount,
    int damageToPlayer1,int damageToPlayer2) {
}
