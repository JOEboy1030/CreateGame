package com.game;

public class SuffixVowelMatcher {

    private final VowelSequenceConverter
            vowelSequenceConverter;

    public SuffixVowelMatcher(VowelSequenceConverter vowelSequenceConverter) {
        if (vowelSequenceConverter == null) {
            throw new IllegalArgumentException("母音変換処理を設定してください。");
        }

        this.vowelSequenceConverter = vowelSequenceConverter;
    }

    public int countMatches(String themeReading, String answerReading
    ) {
        String themeVowels = vowelSequenceConverter.convert(themeReading);

        String answerVowels = vowelSequenceConverter.convert(answerReading);

        int themeIndex = themeVowels.length() - 1;
        int answerIndex = answerVowels.length() - 1;
        int matchCount = 0;

        while (themeIndex >= 0
                && answerIndex >= 0) {

            char themeVowel = themeVowels.charAt(themeIndex);

            char answerVowel = answerVowels.charAt(answerIndex);

            if (themeVowel != answerVowel) {
                break;
            }

            matchCount++;
            themeIndex--;
            answerIndex--;
        }

        return matchCount;
    }

    public boolean isSupportedReading(String reading) {
        return vowelSequenceConverter
                .isSupportedReading(reading);
    }
}
