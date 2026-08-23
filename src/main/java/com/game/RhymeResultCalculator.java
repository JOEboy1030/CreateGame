package com.game;

public class RhymeResultCalculator {

    public RhymeResult calculate(boolean player1Valid,boolean player2Valid,int player1MatchCount,int player2MatchCount) {
        validateMatchCount(player1MatchCount);
        validateMatchCount(player2MatchCount);

        int difference = player1MatchCount - player2MatchCount;

        int damageToPlayer1 = 0;
        int damageToPlayer2 = 0;

        if (difference > 0) {
            damageToPlayer2 = difference;
        } else if (difference < 0) {
            damageToPlayer1 = Math.abs(difference);
        }

        return new RhymeResult(player1Valid,player2Valid,player1MatchCount,player2MatchCount,
            damageToPlayer1,damageToPlayer2);
    }

    private void validateMatchCount(int matchCount) {
        if (matchCount < 0) {
            throw new IllegalArgumentException("母音の一致数は0以上にしてください。");
        }
    }
}
