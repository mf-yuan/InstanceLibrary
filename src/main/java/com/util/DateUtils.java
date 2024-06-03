package com.util;


import com.model.DateFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {


    /**
     * 10位时间戳转Date
     * @param time
     * @return
     */
    public static Date TimestampToDate(Integer time){
        long temp = (long)time*1000;
        Timestamp ts = new Timestamp(temp);
        Date date = new Date();
        try {
            date = ts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String format(Date date) {
        return formatDate(date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String format(Date date, DateFormat dateFormat) {
        return formatDate(date, new SimpleDateFormat(dateFormat.getFormat()));
    }
    public static String format(Date date, String pattern) {
        return formatDate(date, new SimpleDateFormat(pattern));
    }

    public static Date parse(String date, DateFormat dateFormat) {
        return parseDate(date, new SimpleDateFormat(dateFormat.getFormat()));
    }
    public static Date parse(String date, String pattern) {
        return parseDate(date, new SimpleDateFormat(pattern));
    }

    private static String formatDate(Date date, java.text.DateFormat dateFormat) {
        if(date != null) {
            try {
                return dateFormat.format(date);
            } catch (Exception e) {
                return date.toString();
            }
        }
        return "";
    }
    private static Date parseDate(String date, java.text.DateFormat dateFormat) {
        if(date != null) {
            try {
                return dateFormat.parse(date);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static int getYear(Date date){
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.YEAR);
    }



}
