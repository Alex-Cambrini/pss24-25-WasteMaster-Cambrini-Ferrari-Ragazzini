package it.unibo.wastemaster.core.utils;

import java.time.LocalDate;


public class DateUtils {

    private static LocalDate currentDate = LocalDate.now();
    
    public static LocalDate getCurrentDate() {
        return currentDate;        
    }

    public static void setCurrentDate(LocalDate date) {
        currentDate = date;
    }
}
