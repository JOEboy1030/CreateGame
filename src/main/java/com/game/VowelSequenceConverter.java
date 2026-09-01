package com.game;

public class VowelSequenceConverter {

    private static final String SUPPORTED_KANA = "あいうえお"
            + "かきくけこがぎぐげご"
            + "さしすせそざじずぜぞ"
            + "たちつてとだぢづでど"
            + "なにぬねの"
            + "はひふへほばびぶべぼぱぴぷぺぽ"
            + "まみむめも"
            + "やゆよ"
            + "らりるれろ"
            + "わをんゔゐゑ"
            + "ぁぃぅぇぉゃゅょゎっー";

    private static final String SMALL_KANA = "ぁぃぅぇぉゃゅょゎ";

    private static final String A_KANA = "あかがさざただなはばぱまやらわぁゃゎ";

    private static final String I_KANA = "いきぎしじちぢにひびぴみりゐぃ";

    private static final String U_KANA = "うくぐすずつづぬふぶぷむゆるゔぅゅ";

    private static final String E_KANA = "えけげせぜてでねへべぺめれゑぇ";

    private static final String O_KANA = "おこござぞとどのほぼぽもよろをぉょ";

    public String convert(String reading) {
        String normalizedReading = JapaneseTextNormalizer.normalizeReading(reading);

        if (!isSupportedReading(normalizedReading)) {
            throw new IllegalArgumentException("対応していない文字が含まれています。");
        }

        StringBuilder vowels = new StringBuilder();

        for (int index = 0; index < normalizedReading.length(); index++) {
            char kana = normalizedReading.charAt(index);

            if (kana == 'ん' || kana == 'っ') {
                continue;
            }

            if (kana == 'ー') {
                if (vowels.isEmpty()) {
                    throw new IllegalArgumentException("長音記号の前に母音がありません。");
                }

                vowels.append(vowels.charAt(vowels.length() - 1));
                continue;
            }

            if (SMALL_KANA.indexOf(kana) >= 0 && !vowels.isEmpty()) {
                vowels.deleteCharAt(vowels.length() - 1);
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

        boolean hasVowel = false;

        for (int index = 0; index < normalizedReading.length(); index++) {
            char kana = normalizedReading.charAt(index);

            if (SUPPORTED_KANA.indexOf(kana) < 0) {
                return false;
            }

            if (kana == 'ー' && index == 0) {
                return false;
            }

            if (kana != 'ん' && kana != 'っ' && kana != 'ー') {
                hasVowel = true;
            }
        }

        return hasVowel;
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
