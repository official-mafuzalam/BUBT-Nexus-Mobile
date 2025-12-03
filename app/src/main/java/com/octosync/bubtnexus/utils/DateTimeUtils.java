// utils/DateTimeUtils.java
package com.octosync.bubtnexus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String formatDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateTimeString;
        }
    }

    public static String formatTimeAgo(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "Just now";
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = format.parse(dateTimeString);
            Date now = new Date();

            long diff = now.getTime() - past.getTime();

            if (diff < TimeUnit.MINUTES.toMillis(1)) {
                return "Just now";
            } else if (diff < TimeUnit.HOURS.toMillis(1)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
            } else if (diff < TimeUnit.DAYS.toMillis(1)) {
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else {
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            }
        } catch (ParseException e) {
            return dateTimeString;
        }
    }

    public static String formatTimeOnly(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateTimeString;
        }
    }
}