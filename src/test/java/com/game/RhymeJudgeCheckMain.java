package com.game;

public class RhymeJudgeCheckMain {

    private RhymeJudgeCheckMain() {
    }

    public static void main(String[] args) {
        RhymeJudge rhymeJudge = new RhymeJudge();

        RhymeAnswer theme = new RhymeAnswer("最高","さいこう");

        verifySameMatchCount(rhymeJudge,theme);

        verifyPlayer1Wins(rhymeJudge,theme);

        verifyPlayer2Wins(rhymeJudge,theme);

        verifySameWordIsInvalid(rhymeJudge,theme);

        verifyUnsupportedKanaIsInvalid(rhymeJudge,theme);

        System.out.println();
        System.out.println("すべての確認に成功しました。");
    }

    private static void verifySameMatchCount(RhymeJudge rhymeJudge,RhymeAnswer theme) {
        RhymeAnswer player1Answer = new RhymeAnswer("太陽","たいよう");

        RhymeAnswer player2Answer = new RhymeAnswer("対応","たいおう");

        RhymeResult result = rhymeJudge.judge(theme,player1Answer,true,player2Answer,true);

        verify("一致数が同じ場合",result,true,true,4,4,0,0);
    }

    private static void verifyPlayer1Wins(RhymeJudge rhymeJudge,RhymeAnswer theme) {
        RhymeAnswer player1Answer = new RhymeAnswer("太陽","たいよう");

        RhymeAnswer player2Answer = new RhymeAnswer("層","そう");

        RhymeResult result = rhymeJudge.judge(theme,player1Answer,true,player2Answer,true);

        verify("プレイヤー1の一致数が多い場合",result,true,true,4,2,0,2);
    }

    private static void verifyPlayer2Wins(RhymeJudge rhymeJudge,RhymeAnswer theme) {
        RhymeAnswer player1Answer = new RhymeAnswer("層","そう");

        RhymeAnswer player2Answer = new RhymeAnswer("太陽","たいよう");

        RhymeResult result = rhymeJudge.judge(theme,player1Answer,true,player2Answer,true);

        verify("プレイヤー2の一致数が多い場合",result,true,true,2,4,2,0);
    }

    private static void verifySameWordIsInvalid(RhymeJudge rhymeJudge,RhymeAnswer theme) {
        RhymeAnswer player1Answer = new RhymeAnswer("最高","さいこう");

        RhymeAnswer player2Answer = new RhymeAnswer("層","そう");

        RhymeResult result = rhymeJudge.judge(theme,player1Answer,true,player2Answer,true);

        verify("お題と同じ単語の場合",result,false,true,0,2,2,0);
    }

    private static void verifyUnsupportedKanaIsInvalid(RhymeJudge rhymeJudge,RhymeAnswer theme) {
        RhymeAnswer player1Answer =new RhymeAnswer("学校","がっこう");

        RhymeAnswer player2Answer = new RhymeAnswer("層","そう");

        RhymeResult result = rhymeJudge.judge(theme,player1Answer,true,player2Answer,true);

        verify("対応外の文字が含まれる場合",result,false,true,0,2,2,0);
    }

    private static void verify(String testName,RhymeResult actual,boolean expectedPlayer1Valid,boolean expectedPlayer2Valid,
            int expectedPlayer1MatchCount,int expectedPlayer2MatchCount,int expectedDamageToPlayer1,int expectedDamageToPlayer2
    ) {
        boolean success = actual.player1Valid() == expectedPlayer1Valid && actual.player2Valid() 
        == expectedPlayer2Valid && actual.player1MatchCount() == expectedPlayer1MatchCount && actual.player2MatchCount() 
        == expectedPlayer2MatchCount && actual.damageToPlayer1() == expectedDamageToPlayer1 && actual.damageToPlayer2() 
        == expectedDamageToPlayer2;

        if (!success) {
            throw new AssertionError(testName + "に失敗しました。実際の結果: " + actual);
        }

        System.out.println("[PASS] " + testName + " -> " + actual);
    }
}
