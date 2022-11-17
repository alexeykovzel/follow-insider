package com.alexeykovzel.fi.common;

import java.util.Set;

public class StringUtils {
    private final static Set<String> DOTTED_WORDS = Set.of("Corp", "Inc", "Ltd", "Co");

    public static String toCamelCase(String val) {
        Set<Character> reservedChars = Set.of(' ', '.');
        char[] chars = val.toLowerCase().toCharArray();
        char[] capitalizedVal = new char[chars.length];
        char prevChar = ' ';
        for (int i = 0; i < chars.length; i++) {
            capitalizedVal[i] = reservedChars.contains(prevChar)
                    ? Character.toUpperCase(chars[i]) : chars[i];
            prevChar = chars[i];
        }
        return String.valueOf(capitalizedVal);
    }

    public static String addDots(String val) {
        String[] words = val.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (DOTTED_WORDS.contains(word)) {
                words[i] = word + ".";
            }
        }
        return String.join(" ", words);
    }

    public static String addLeadingZeros(String val) {
        return "0".repeat(10 - val.length()) + val;
    }

    public static String trimLeadingZeros(String val) {
        return val.replaceFirst("^0+(?!$)", "");
    }

    public static String formatNumber(double val) {
        if (val > 1.0e+9) return String.format("%.2fB", val / 1.0e+9);
        if (val > 1.0e+6) return String.format("%.2fM", val / 1.0e+9);
        if (val > 1.0e+3) return String.format("%.2fK", val / 1.0e+9);
        return String.format("%.2f", val);
    }
}
