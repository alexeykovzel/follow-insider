package com.alexeykovzel.fi.core.trade;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TradeCode {

    /* General trade codes */
    REPORTED_EARLIER("V", "Other", "-"),
    PURCHASE("P", "Buy", "-"),
    SALE("S", "Sell", "-"),

    /* Rule 16b-3 trade codes */
    SALE_BACK_TO_ISSUER("D", "Sell", "-"),
    LIABILITY_EXERCISE("F", "Taxes", "-"),
    OPTION_EXERCISE("M", "Options", "-"),
    GRANT_OR_AWARD("A", "Grant", "-"),
    DISCRETIONARY("I", "Other", "-"),

    /* Derivative securities codes (usually options) */
    OPTION_EXERCISE_OUT_OF_MONEY("O", "Options", "-"),
    OPTION_EXERCISE_IN_MONEY("X", "Options", "-"),
    OPTION_EXPIRATION_SHORT("E", "Options", "-"),
    OPTION_EXPIRATION_LONG("H", "Options", "-"),
    OPTION_CONVERSION("C", "Options", "-"),

    /* Other sections 16b exempt trades and small acquisition codes */
    SMALL_ACQUISITION("L", "Other", "-"),
    BONA_FIDE_GIFT("G", "Other", "-"),
    VOTING_TRUST("Z", "Other", "-"),
    INHERITED("W", "Other", "-"),

    /* Other trade codes */
    CHANGE_OF_CONTROL("U", "Other", "-"),
    EQUITY_SWAP("K", "Other", "-"),
    OTHER("J", "Other", "-");

    public final String code;
    public final String value;
    public final String description;

    public static String codeOfValue(String value) {
        for (TradeCode type : values()) {
            if (type.value.equals(value)) {
                return type.code;
            }
        }
        return null;
    }

    public static String valueOfCode(String code) {
        for (TradeCode type : values()) {
            if (type.code.equals(code)) {
                return type.value;
            }
        }
        return null;
    }
}
