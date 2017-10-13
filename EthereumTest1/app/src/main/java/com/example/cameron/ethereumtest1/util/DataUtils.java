package com.example.cameron.ethereumtest1.util;

import java.util.Date;

/**
 * Created by cameron on 10/13/17.
 */

public class DataUtils {

    public static String convertTimeStampToDateString(long timestamp) {
        Date date = new Date(timestamp);
        long now = System.currentTimeMillis();
        if ((now - timestamp) < (1000 * 60 * 60)) {
            long timePassed = now - timestamp;
            long hours = timePassed / (1000 * 60);
            return hours + " mins ago";
        } else if ((now - timestamp) < (1000 * 60 * 60 * 24)) {
            long timePassed = now - timestamp;
            long hours = timePassed / (1000 * 60 * 60);
            return hours + " hours ago";
        }
        return date.getMonth() + "/" + date.getDay() + "/" + date.getYear();
    }
}
