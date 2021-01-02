package com.example.managerheathcareapp.Functions;

import android.content.Context;
import android.text.format.DateFormat;


import com.example.managerheathcareapp.R;

import java.util.Calendar;
import java.util.Locale;

public class TimeFunc {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final int MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final int YEAR_MILLIS = 12 * MONTH_MILLIS;


    public static String getTimeAgo(long time, Context context) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) { //2000
            return context.getResources().getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) { //
            return diff / MINUTE_MILLIS + " " + context.getResources().getString(R.string.minutes_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.an_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " " + context.getResources().getString(R.string.hours_ago);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.yesterday);
        } else {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(time);
            String dateTimeMes = DateFormat.format("dd/MM/yyyy hh:mm", calendar).toString();
            return diff / DAY_MILLIS + " " + context.getResources().getString(R.string.days_ago);
        }
    }

}
