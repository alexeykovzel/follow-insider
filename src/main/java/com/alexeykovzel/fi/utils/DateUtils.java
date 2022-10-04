package com.alexeykovzel.fi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final String EDGAR_FORMAT = "yyyy-MM-dd";

    public static Date shiftYears(Date date, int years) {
        return shiftTime(date, TimeUnit.DAYS, years * 365);
    }

    public static Date shiftMonths(Date date, int months) {
        return shiftTime(date, TimeUnit.DAYS, months * 30);
    }

    public static Date shiftDays(Date date, int days) {
        return shiftTime(date, TimeUnit.DAYS, days);
    }

    public static Date shiftHours(Date date, int hours) {
        return shiftTime(date, TimeUnit.HOURS, hours);
    }

    public static Date shiftSeconds(Date date, int seconds) {
        return shiftTime(date, TimeUnit.SECONDS, seconds);
    }

    private static Date shiftTime(Date date, TimeUnit timeUnit, int duration) {
        return new Date(date.getTime() + timeUnit.toMillis(duration));
    }

    public static Date shiftRange(Date date, String range) {
        range = range.toUpperCase();
        if ("MAX".equals(range)) return new Date(0);
        try {
            int lastIdx = range.length() - 1;
            int duration = -Integer.parseInt(range.substring(0, lastIdx));
            String metric = range.substring(lastIdx);
            if ("D".equals(metric)) return shiftDays(date, duration);
            if ("M".equals(metric)) return shiftMonths(date, duration);
            if ("Y".equals(metric)) return shiftYears(date, duration);
        } catch (NumberFormatException ignored) {
        }
        throw new IllegalArgumentException("Invalid range: " + range);
    }

    public static int yearsBetween(Date d1, Date d2) {
        return monthsBetween(d1, d2) / 12;
    }

    public static int monthsBetween(Date d1, Date d2) {
        return daysBetween(d1, d2) / 30;
    }

    public static int daysBetween(Date d1, Date d2) {
        return hoursBetween(d1, d2) / 24;
    }

    public static int hoursBetween(Date d1, Date d2) {
        return secondsBetween(d1, d2) / 60;
    }

    public static int secondsBetween(Date d1, Date d2) {
        long timeDifference = d2.getTime() - d1.getTime();
        long secondTime = TimeUnit.SECONDS.toMillis(1);
        return (int) (timeDifference / secondTime);
    }

    public static Date parseEdgar(String date) {
        return parse(date, EDGAR_FORMAT);
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
