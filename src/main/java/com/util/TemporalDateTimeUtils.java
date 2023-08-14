package com.util;

//import com.model.DateFiled;
import com.model.DateFormat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.*;
import java.util.Date;

/**
 * 使用java.time中的类来实现
 * @author yuanmengfan
 * @date 2022/3/26 10:09 下午
 * @description
 */
public class TemporalDateTimeUtils extends TemporalAccessorUtils {
    /**
     * 获取10位时间戳
     * @param temporalAccessor
     * @return java.lang.Long
     * @title getEpochSecond
     * @author yuanmengfan
     * @date 2022/3/26 10:32 下午
     */
    public static Long getEpochSecond(TemporalAccessor temporalAccessor) {
        return toInstant(temporalAccessor).getEpochSecond();
    }

    /**
     * 获取13位时间戳
     * @param temporalAccessor 时间对象
     * @return java.lang.Long
     * @title getEpochMilli
     * @author yuanmengfan
     * @date 2022/3/26 10:30 下午
     */
    public static Long getEpochMilli(TemporalAccessor temporalAccessor) {
        return toInstant(temporalAccessor).toEpochMilli();
    }

    /**
     * 13位时间戳到本地日期时间
     * @param timestamp
     * @return java.time.LocalDateTime
     * @title timestampToLocalDateTime
     * @author yuanmengfan
     * @date 2022/3/26 10:36 下午
     */
    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 10位时间戳到本地日期时间
     * @param timestamp
     * @return java.time.LocalDateTime
     * @title unixTimestampToLocalDateTime
     * @author yuanmengfan
     * @date 2022/3/26 10:37 下午
     */
    public static LocalDateTime unixTimestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    /**
     * 获取一天最开始的的时间
     * @param localDate
     * @return java.time.LocalDateTime
     * @title getStartDateTime
     * @author yuanmengfan
     * @date 2022/3/26 10:46 下午
     */
    public static LocalDateTime getDayStartDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * 获取这一天最后的
     * @param localDate
     * @return java.time.LocalDateTime
     * @title getEndDateTime
     * @author yuanmengfan
     * @date 2022/3/26 11:03 下午
     */
    public static LocalDateTime getDayEndDateTime(LocalDate localDate) {
        return localDate.atStartOfDay().plusDays(1).plus(-1, ChronoUnit.MILLIS);
    }

    /**
     * 获取今天是几号
     * @title getNowDayOfYear
     * @return int
     * @author yuanmengfan
     * @date 2022/3/26 11:18 下午
     */
    public static int getNowDayOfMonth(){
        return LocalDate.now().getDayOfMonth();
    }

    /**
     * 今天是哪一年
     * @title getNowYear
     * @return int
     * @author yuanmengfan
     * @date 2022/3/26 11:19 下午
     */
    public static int getNowYear(){
        return LocalDate.now().getYear();
    }

    /**
     * 今天是几月
     * @title getNowMonth
     * @return int
     * @author yuanmengfan
     * @date 2022/3/26 11:19 下午
     */
    public static int getNowMonth(){
        return LocalDate.now().getMonthValue();
    }

    /**
     * 当前月第几个星期
     * @title weekOfMonth
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:46 下午
     */
    public static int weekOfMonth(LocalDate localDate){
       return localDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
    }

    /**
     * 当前年第几个星期
     * @title weekOfYear
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:47 下午
     */
    public static int weekOfYear(LocalDate localDate){
        return localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    /**
     * 当前年第几天
     * @title dayOfYear
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:47 下午
     */
    public static int dayOfYear(LocalDate localDate){
        return localDate.getDayOfYear();
    }

    /**
     * 当前年第几月
     * @title dayOfWeekValue
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:47 下午
     */
    public static int dayOfWeekValue(LocalDate localDate){
        return localDate.getDayOfWeek().getValue();
    }
    /**
     * 当前年第几月 {@link DayOfWeek}
     * @title dayOfWeekValue
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:47 下午
     */
    public static DayOfWeek dayOfWeek(LocalDate localDate){
        return localDate.getDayOfWeek();
    }

    /**
     * 是上午吗 00:00:00为上午 12:00:00为下午
     * @title isMorning
     * @param localTime
     * @return boolean
     * @author yuanmengfan
     * @date 2022/3/29 1:48 下午
     */
    public static boolean isMorning(LocalTime localTime){
        return localTime.get(ChronoField.AMPM_OF_DAY) == 0;
    }
    /**
     * 是下午吗 00:00:00为上午 12:00:00为下午
     * @title isMorning
     * @param localTime
     * @return boolean
     * @author yuanmengfan
     * @date 2022/3/29 1:48 下午
     */
    public static boolean isAfternoon(LocalTime localTime){
        return !isMorning(localTime);
    }

    /**
     * 当前年的季度
     * @title getQuarter
     * @param localDate
     * @return int
     * @author yuanmengfan
     * @date 2022/3/29 1:49 下午
     */
    public static int getQuarter(LocalDate localDate){
        return (localDate.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * 当前年的季度字符串 返回样例 202201
     * @title getQuarterAndYear
     * @param localDate
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/29 1:50 下午
     */
    public static String getQuarterAndYear(LocalDate localDate){
        return String.format("%d%02d", localDate.getYear(),getQuarter(localDate));
    }

    /**
     * 指定日期的某个字段为最开始的时间
     * 例 2022-02-27 12:12:12 ChronoUnit.DayOfMonth
     * 得到2022-02-01 12:12:12
     * @title getBeginDateValue
     * @param localDateTime
     * @param temporalField
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/29 2:01 下午
     */
    public static LocalDateTime getBeginDateValue(LocalDateTime localDateTime, TemporalField temporalField){
        return localDateTime.with(temporalField,temporalField.range().getMinimum());
    }
    /**
     * 指定日期的某个字段为最结束的时间 支持动态的月份
     * 例 2022-02-27 12:12:12 ChronoUnit.DayOfMonth
     * 得到2022-02-01 12:12:12
     * @title getBeginDateValue
     * @param localDateTime
     * @param temporalField
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/29 2:01 下午
     */
    public static LocalDateTime getEndDateValue(LocalDateTime localDateTime, TemporalField temporalField){
        if(temporalField == ChronoField.DAY_OF_MONTH){
           return localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        }
        return localDateTime.with(temporalField,temporalField.range().getMaximum());
    }

    /**
     * 获得当前日期指定 temporalField 的开始时间 包括子字段
     * 例1、LocalDateTime = now() TemporalField = DateFiled.Day
     *      2022-03-29 13:50:00 置换后为 2022-03-01 00:00:00
     * 例2、LocalDateTime = now() DateFiled = DateFiled.Mon
     *      2022-03-29 13:50:00 置换后为 2022-03-01 00:00:00
     * @title getBeginDateValue
     * @param localDateTime
     * @param dateFiled
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/29 1:50 下午
     */
//    public static LocalDateTime getBeginDateSubValue(LocalDateTime localDateTime, DateFiled dateFiled){
//        if(dateFiled.getChronoField() == ChronoField.YEAR){
//            return getEpochDateTime();
//        }
//        for (int i = dateFiled.getSortIndex(); i < DateFiled.values().length + 1 ; i++) {
//            localDateTime = getBeginDateValue(localDateTime,DateFiled.of(i).getChronoField());
//        }
//        return localDateTime;
//    }
    /**
     * 获得当前日期指定 temporalField 的结束时间 包括子字段
     * 例1、LocalDateTime = now() TemporalField = DateFiled.Day
     * 2022-03-29 13:50:00 置换后为 2022-03-31 23:59:59
     * LocalDateTime = now() TemporalField = DateFiled.Month
     * 2022-03-29 13:50:00 置换后为 2022-12-31 23:59:59
     * @title getEndDateSubValue
     * @param localDateTime
     * @param dateFiled
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:08 下午
     */
//    public static LocalDateTime getEndDateSubValue(LocalDateTime localDateTime, DateFiled dateFiled){
//        for (int i = dateFiled.getSortIndex(); i < DateFiled.values().length + 1 ; i++) {
//            localDateTime = getEndDateValue(localDateTime,DateFiled.of(i).getChronoField());
//        }
//        return localDateTime;
//    }

    /**
     * 获得两个日期之间的某个字段的差值
     * @title between
     * @param start 开始时间
     * @param end   结束时间
     * @param temporalUnit  需要拿到差值的字段
     * @param isAbs    是否需要绝对值
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:11 下午
     */
    public static long between(LocalDateTime start,LocalDateTime end,TemporalUnit temporalUnit,boolean isAbs){
        long between = temporalUnit.between(start, end);
        if(isAbs ||between < 0){
            between = Math.abs(between);
        }
        return between;
    }
    /**
     *  获得两个日期之间的某个字段的差值  结果为正数
     * @title between
     * @param start 开始时间
     * @param end   结束时间
     * @param temporalUnit  需要拿到差值的字段
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long between(LocalDateTime start,LocalDateTime end,TemporalUnit temporalUnit){
        return Math.abs(temporalUnit.between(start, end));
    }
    /**
     *  获得两个日期之间的相差多少年
     * @title betweenOfYears
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfYears(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.YEARS);
    }
    /**
     *  获得两个日期之间的相差多少月
     * @title betweenOfMonths
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfMonths(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.MONTHS);
    }
    /**
     *  获得两个日期之间的相差多少天
     * @title betweenOfDays
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfDays(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.DAYS);
    }
    /**
     *  获得两个日期之间的相差多少小时
     * @title betweenOfHours
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfHours(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.HOURS);
    }
    /**
     *  获得两个日期之间的相差多少分钟
     * @title betweenOfMinutes
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfMinutes(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.MINUTES);
    }
    /**
     *  获得两个日期之间的相差多少秒
     * @title betweenOfSeconds
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long betweenOfSeconds(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.SECONDS);
    }
    /**
     *  获得两个日期之间的相差多少毫秒
     * @title betweenOfSeconds
     * @param start 开始时间
     * @param end   结束时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static Long betweenOfMillis(LocalDateTime start,LocalDateTime end){
        return between(start,end,ChronoUnit.MILLIS);
    }
    /**
     *  获得纪元时间到现在某个字段的差值
     * @title getEpochToNow
     * @param dateTime 开始时间
     * @return long
     * @author yuanmengfan
     * @date 2022/3/30 1:12 下午
     */
    public static long getEpochToNow(LocalDateTime dateTime,TemporalUnit temporalUnit){
        return between(getEpochDateTime(),dateTime,temporalUnit);
    }

    /**
     * 将时间对象已默认的字符串形式返回 yyyy-MM-dd HH:mm:ss
     * @title format
     * @param temporalAccessor
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:47 下午
     */
    public static String format(TemporalAccessor temporalAccessor){
        return toLocalDateTime(temporalAccessor).format(getDateTimeFormatter(DateFormat.DEFAULT_STANDARD_DATE_PATTERN));
    }
    /**
     * 将Date对象已默认的字符串形式返回 yyyy-MM-dd HH:mm:ss
     * @title format
     * @param date
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:47 下午
     */
    public static String format(Date date){
        return toLocalDateTime(date).format(getDateTimeFormatter(DateFormat.DEFAULT_STANDARD_DATE_PATTERN));
    }

    /**
     * 将时间对象已自定义（patten）的字符串形式返回
     * @title format
     * @param temporalAccessor
     * @param pattern
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:48 下午
     */
    public static String format(TemporalAccessor temporalAccessor,String pattern){
        return toLocalDateTime(temporalAccessor).format(getDateTimeFormatter(pattern));
    }
    /**
     * 将Date对象已自定义（patten）的字符串形式返回
     * @title format
     * @param date
     * @param pattern
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:48 下午
     */
    public static String format(Date date,String pattern){
        return toLocalDateTime(date).format(getDateTimeFormatter(pattern));
    }
    /**
     * 将时间对象已自定义（DateFormat）的字符串形式返回
     * @title format
     * @param temporalAccessor
     * @param dateFormat
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:49 下午
     */
    public static String format(TemporalAccessor temporalAccessor,DateFormat dateFormat){
        return toLocalDateTime(temporalAccessor).format(getDateTimeFormatter(dateFormat));
    }
    /**
     * 将Date对象已自定义（DateFormat）的字符串形式返回
     * @title format
     * @param date
     * @param dateFormat
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/3/27 4:48 下午
     */
    public static String format(Date date,DateFormat dateFormat){
        return toLocalDateTime(date).format(getDateTimeFormatter(dateFormat));
    }

    /**
     * {@link String} 转 {@link LocalDateTime}
     * @title parse
     * @param dateStr 日期字符串
     * @param pattern 格式化字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static LocalDateTime parse(String dateStr,String pattern){
       return toLocalDateTime(getDateTimeFormatter(pattern).parse(dateStr));
    }
    /**
     * {@link String} 转 {@link LocalDateTime} 根据 {@see DateFormat} 来转换
     * @title parse
     * @param dateStr 日期字符串
     * @param pattern 格式化字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static LocalDateTime parse(String dateStr,DateFormat pattern){
        return toLocalDateTime(getDateTimeFormatter(pattern).parse(dateStr));
    }
    /**
     * {@link String} 转 {@link LocalDateTime} 默认格式化字符串为 "yyyy-MM-dd HH:mm:ss"
     * {@code DateFormat.DEFAULT_STANDARD_DATE_PATTERN}
     * @title parse
     * @param dateStr 日期字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static LocalDateTime parse(String dateStr){
        return parse(dateStr,DateFormat.DEFAULT_STANDARD_DATE_PATTERN);
    }
    /**
     * {@link String} 转 {@link Date}
     * @title parseDate
     * @param dateStr 日期字符串
     * @param pattern 格式化字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static Date parseDate(String dateStr,String pattern){
        return toDate(getDateTimeFormatter(pattern).parse(dateStr));
    }
    /**
     * {@link String} 转 {@link Date}
     * @title parseDate
     * @param dateStr 日期字符串
     * @param pattern 格式化字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static Date parseDate(String dateStr,DateFormat pattern){
        return toDate(getDateTimeFormatter(pattern).parse(dateStr));
    }
    /**
     * {@link String} 转 {@link Date} 默认格式化字符串为 "yyyy-MM-dd HH:mm:ss"
     * {@code DateFormat.DEFAULT_STANDARD_DATE_PATTERN}
     * @title parseDate
     * @param dateStr 日期字符串
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/30 1:40 下午
     */
    public static Date parseDate(String dateStr){
        return parseDate(dateStr,DateFormat.DEFAULT_STANDARD_DATE_PATTERN);
    }

    /**
     * 通过patten字符串创建一个DateTimeFormatter 对象
     * @title getDateTimeFormatter
     * @param pattern
     * @return java.time.format.DateTimeFormatter
     * @author yuanmengfan
     * @date 2022/3/27 4:50 下午
     */
    private static DateTimeFormatter getDateTimeFormatter(String pattern){
        return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter();
    }
    /**
     * 通过DateFormat字符串创建一个DateTimeFormatter 对象
     * @title getDateTimeFormatter
     * @param dateFormat
     * @return java.time.format.DateTimeFormatter
     * @author yuanmengfan
     * @date 2022/3/27 4:50 下午
     */
    private static DateTimeFormatter getDateTimeFormatter(DateFormat dateFormat){
        return getDateTimeFormatter(dateFormat.getFormat());
    }


    public static <T extends TemporalAccessor> boolean isBetween(T target,T start,T end){
        return  getEpochMilli(target) >=  getEpochMilli(start) && getEpochMilli(target) <=  getEpochMilli(end);
    }
    public static  <T extends TemporalAccessor> boolean isBeyond(T target,T start,T end){
        return !isBetween(target,start, end);
    }
}
