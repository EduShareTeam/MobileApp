package com.fatihbaser.edusharedemo.utils;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
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
            return "Az Önce";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Bir dakika önce";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "önce " + diff / MINUTE_MILLIS + " dakika";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Bir saat önce";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Önce " + diff / HOUR_MILLIS + " saat";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Dün";
        } else {
            return "Önceki " + diff / DAY_MILLIS + " günler";
        }
    }

    public static String timeFormatAMPM(long timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        String dateString = formatter.format(new Date(timestamp));

        return  dateString;
    }

}

