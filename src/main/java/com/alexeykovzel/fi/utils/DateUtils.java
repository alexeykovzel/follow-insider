package com.alexeykovzel.fi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    public static Date shiftMonths(Date date, int months) {
        return shiftTime(date, TimeUnit.DAYS, months * 30);
    }

    public static Date shiftSeconds(Date date, int seconds) {
        return shiftTime(date, TimeUnit.SECONDS, seconds);
    }

    private static Date shiftTime(Date date, TimeUnit timeUnit, int duration) {
        return new Date(date.getTime() + timeUnit.toMillis(duration));
    }

    public static int monthsBetween(Date d1, Date d2) {
        long timeDifference = d2.getTime() - d1.getTime();
        long monthTime = TimeUnit.DAYS.toMillis(1) * 30;
        return (int) (timeDifference / monthTime);
    }

    public static Date parse(String date) {
        return parse(date, DEFAULT_FORMAT);
    }

    public static Date parse(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            System.out.println("[ERROR] Could not parse date: " + e.getMessage());
            return null;
        }
    }
}
