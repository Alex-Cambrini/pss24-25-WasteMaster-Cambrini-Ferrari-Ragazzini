package it.unibo.wastemaster.core.utils;

import java.util.Date;

public class DateUtils {

    private static Date currentDate = new Date();
    public static Date getCurrentDate() {
        return currentDate;        
    }

    public static void setCurrentDate(Date date) {
        currentDate = date;
    }
}
