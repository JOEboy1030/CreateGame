package com.game;

public class RhymeJudge {

    private final SuffixVowelMatcher suffixVowelMatcher;

    private final RhymeResultCalculator rhymeResultCalculator;

    public RhymeJudge() {
        this(new SuffixVowelMatcher(new VowelSequenceConverter()),new RhymeResultCalculator());
    }

    public RhymeJudge(SuffixVowelMatcher suffixVowelMatcher) {
        this(suffixVowelMatcher,new RhymeResultCalculator());
    }

    public RhymeJudge(SuffixVowelMatcher suffixVowelMatcher,RhymeResultCalculator rhymeResultCalculator) {
        if (suffixVowelMatcher == null) {
            throw new IllegalArgumentException("母音一致判定を設定してください。");
        }

        if (rhymeResultCalculator == null) {
            throw new IllegalArgumentException("結果計算処理を設定してください。");
        }

        this.suffixVowelMatcher = suffixVowelMatcher;

        this.rhymeResultCalculator = rhymeResultCalculator;
    }

    public RhymeResult judge(RhymeAnswer theme,RhymeAnswer player1Answer,boolean player1ExternalValid,
        RhymeAnswer player2Answer,boolean player2ExternalValid
    ) {
        validateTheme(theme);

        boolean player1Valid = player1ExternalValid && isAnswerValid(theme,player1Answer);

        boolean player2Valid = player2ExternalValid && isAnswerValid(theme,player2Answer);

        int player1MatchCount = calculateMatchCount(theme,player1Answer,player1Valid);

        int player2MatchCount = calculateMatchCount(theme,player2Answer,player2Valid);

        return rhymeResultCalculator.calculate(player1Valid,player2Valid,player1MatchCount,player2MatchCount);
    }

    private void validateTheme(RhymeAnswer theme) {
        if (theme == null || theme.word() == null || theme.word().isBlank() || theme.reading() == null || theme.reading().isBlank()) {
            throw new IllegalArgumentException("お題の単語と読みを設定してください。");
        }

        if (!suffixVowelMatcher.isSupportedReading(theme.reading())) {
            throw new IllegalArgumentException("お題に対応していない文字が含まれています。");
        }
    }

    private boolean isAnswerValid(RhymeAnswer theme,RhymeAnswer answer) {
        if (answer == null || answer.word() == null || answer.word().isBlank() || answer.reading() == null || answer.reading().isBlank()) {
            return false;
        }

        if (!suffixVowelMatcher.isSupportedReading(answer.reading())) {
            return false;
        }

        String normalizedThemeWord = JapaneseTextNormalizer.normalizeSurface(theme.word());

        String normalizedAnswerWord = JapaneseTextNormalizer.normalizeSurface(answer.word());

        return !normalizedThemeWord.equals(normalizedAnswerWord);
    }

    private int calculateMatchCount(RhymeAnswer theme, RhymeAnswer answer,boolean valid) {
        if (!valid) {
            return 0;
        }

        return suffixVowelMatcher.countMatches(theme.reading(),answer.reading());
    }
}
