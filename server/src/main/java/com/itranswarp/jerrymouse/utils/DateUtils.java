package com.itranswarp.jerrymouse.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    static final ZoneId GMT = ZoneId.of("Z");

    public static long parseDateTimeGMT(String s) {
        ZonedDateTime zdt = ZonedDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME);
        return zdt.toInstant().toEpochMilli();
    }

    public static String formatDateTimeGMT(long ts) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), GMT);
        return zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
