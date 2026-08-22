package com.game;

import java.text.Normalizer;

public class JapaneseTextNormalizer {

    private JapaneseTextNormalizer() {
    }

    public static String normalizeSurface(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value.trim(),Normalizer.Form.NFKC
        );
    }

    public static String normalizeReading(String value) {
        String normalizedValue = normalizeSurface(value);

        StringBuilder builder = new StringBuilder();

        normalizedValue.codePoints()
                .map(JapaneseTextNormalizer::convertKatakanaToHiragana)
                .forEach(builder::appendCodePoint);

        return builder.toString();
    }

    private static int convertKatakanaToHiragana(
            int codePoint
    ) {
        if (codePoint >= 0x30A1 && codePoint <= 0x30F6
        ) {
            return codePoint - 0x60;
        }

        return codePoint;
    }
}
