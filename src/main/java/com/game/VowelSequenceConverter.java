package com.game;

public class VowelSequenceConverter {

    private static final String SUPPORTED_KANA = "あいうえお"
            + "かきくけこ"
            + "さしすせそ"
            + "たちつてと"
            + "なにぬねの"
            + "はひふへほ"
            + "まみむめも"
            + "やゆよ"
            + "らりるれろ"
            + "わをん";

    private static final String A_KANA = "あかさたなはまやらわ";

    private static final String I_KANA = "いきしちにひみり";

    private static final String U_KANA = "うくすつぬふむゆる";

    private static final String E_KANA = "えけせてねへめれ";

    private static final String O_KANA = "おこそとのほもよろを";

    public String convert(String reading) {
        String normalizedReading = JapaneseTextNormalizer.normalizeReading(reading);

        if (!isSupportedReading(normalizedReading)) {
            throw new IllegalArgumentException("対応していない文字が含まれています。");
        }

        StringBuilder vowels = new StringBuilder();

        for (int index = 0; index < normalizedReading.length(); index++) {

            char kana = normalizedReading.charAt(index);

            if (kana == 'ん') {
                continue;
            }

            vowels.append(findVowel(kana));
        }

        return vowels.toString();
    }

    public boolean isSupportedReading(String reading) {
        String normalizedReading = JapaneseTextNormalizer.normalizeReading(reading);

        if (normalizedReading.isBlank()) {
            return false;
        }

        for (int index = 0; index < normalizedReading.length(); index++) {

            char kana = normalizedReading.charAt(index);

            if (SUPPORTED_KANA.indexOf(kana) < 0) {
                return false;
            }
        }

        return true;
    }

    private char findVowel(char kana) {
        if (A_KANA.indexOf(kana) >= 0) {
            return 'a';
        }

        if (I_KANA.indexOf(kana) >= 0) {
            return 'i';
        }

        if (U_KANA.indexOf(kana) >= 0) {
            return 'u';
        }

        if (E_KANA.indexOf(kana) >= 0) {
            return 'e';
        }

        if (O_KANA.indexOf(kana) >= 0) {
            return 'o';
        }

        throw new IllegalArgumentException("母音へ変換できない文字です: " + kana);
    }
}
