package com.example.medication_reminder_app.data.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static final SimpleDateFormat DISPLAY_FMT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final SimpleDateFormat DATETIME_FMT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static String toDateString(long timestamp) {
        return DATE_FMT.format(new Date(timestamp));
    }

    public static String toDisplayDate(long timestamp) {
        return DISPLAY_FMT.format(new Date(timestamp));
    }

    public static String toDisplayDateTime(long timestamp) {
        return DATETIME_FMT.format(new Date(timestamp));
    }

    public static String today() {
        return DATE_FMT.format(new Date());
    }

    public static long toStartOfDay(String dateStr) {
        try {
            Date d = DATE_FMT.parse(dateStr);
            return d != null ? d.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}