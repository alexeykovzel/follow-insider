package com.alexeykovzel.fi.common;

import java.util.Set;

public class StringUtils {
    private final static Set<String> DOTTED_WORDS = Set.of("Corp", "Inc", "Ltd", "Co");

    public static String toCamelCase(String value) {
        char[] chars = value.toLowerCase().toCharArray();
        char[] result = new char[chars.length];
        char prev = ' ';
        for (int i = 0; i < chars.length; i++) {
            result[i] = prev == ' ' || prev == '.'
                    ? Character.toUpperCase(chars[i])
                    : chars[i];
            prev = chars[i];
        }
        return String.valueOf(result);
    }

    public static String addDots(String value) {
        String[] words = value.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (DOTTED_WORDS.contains(word)) {
                words[i] = word + ".";
            }
        }
        return String.join(" ", words);
    }

    public static String addLeadingZeros(String value) {
        return "0".repeat(10 - value.length()) + value;
    }

    public static String trimLeadingZeros(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }

    public static String formatNumber(double num) {
        if (num > 1.0e+9) return String.format("%.2fB", num / 1.0e+9);
        if (num > 1.0e+6) return String.format("%.2fM", num / 1.0e+9);
        if (num > 1.0e+3) return String.format("%.2fK", num / 1.0e+9);
        return String.format("%.2f", num);
    }
}
