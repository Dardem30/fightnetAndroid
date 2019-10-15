package com.roman.fightnet.util;

import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarUtil {
    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM");
    private final static SimpleDateFormat standartDateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    private final static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
    private final static SimpleDateFormat serverFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String formatCreateTimeForMessage(final Date date) {
        if (DateUtils.isSameDay(date, new Date())) {
            return timeFormatter.format(date);
        }
        return dateFormatter.format(date);
    }
    public static String formatDateTime(final Date date) {
        return dateTimeFormatter.format(date);
    }
    public static String serverFormat(final Date date) {
        return serverFormatter.format(date);
    }

    public static Date parseDateFormat(final String date) {
        try {
            return standartDateFormatter.parse(date);
        } catch (final ParseException e) {
            Log.e("CalendarUtil", "Error during parsing date", e);
            throw new RuntimeException();
        }
    }
}
