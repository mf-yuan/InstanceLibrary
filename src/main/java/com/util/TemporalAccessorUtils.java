package com.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yuanmengfan
 * @date 2022/3/27 5:01 下午
 * @description
 */
public class TemporalAccessorUtils {

    /**
     * // {@link TemporalAccessor}转换为 {@link Instant}对象
     * @title toInstant
     * @param temporalAccessor
     * @return java.time.Instant
     * @author yuanmengfan
     * @date 2022/3/27 5:22 下午
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((LocalDate) temporalAccessor).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 返回为纪元开始的时间 1970-1-1 LocalTime
            result = ((LocalTime) temporalAccessor).atDate(getEpochDate()).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((OffsetTime) temporalAccessor).atDate(getEpochDate()).toInstant();
        } else {
            result = Instant.from(temporalAccessor);
        }
        return result;
    }

    /**
     * // {@link TemporalAccessor} 转 {@link ZonedDateTime}
     * @title toZoneDateTime
     * @param temporalAccessor 时间对象
     * @return java.time.ZonedDateTime
     * @author yuanmengfan
     * @date 2022/3/26 11:48 下午
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporalAccessor,ZoneId zoneId){
        if (temporalAccessor == null) {
            return null;
        }
        ZonedDateTime result;
        if (temporalAccessor instanceof Instant) {
            result = ZonedDateTime.ofInstant((Instant) temporalAccessor, zoneId);
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result =  ((ZonedDateTime) temporalAccessor).withZoneSameInstant(zoneId);
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toZonedDateTime();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(zoneId);
        } else if (temporalAccessor instanceof LocalDate) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((LocalDate) temporalAccessor).atStartOfDay().atZone(zoneId);
        } else if (temporalAccessor instanceof LocalTime) {
            // 返回为纪元开始的时间 1970-1-1 LocalTime
            result = ((LocalTime) temporalAccessor).atDate(getEpochDate()).atZone(zoneId);
        } else if (temporalAccessor instanceof OffsetTime) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((OffsetTime) temporalAccessor).atDate(getEpochDate()).atZoneSameInstant(zoneId);
        } else {
            result = ZonedDateTime.from(temporalAccessor);
        }
        return result;
    }

    /**
     * // 默认为本地的时区 {@link TemporalAccessor} 转 {@link ZonedDateTime}
     * @title toZonedDateTime
     * @param temporalAccessor
     * @return java.time.ZonedDateTime
     * @author yuanmengfan
     * @date 2022/3/27 5:44 下午
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporalAccessor){
        return toZonedDateTime(temporalAccessor,ZoneId.systemDefault());
    }

    /**
     * // {@link TemporalAccessor} 转 {@link LocalDateTime}
     * @title toZoneDateTime
     * @param temporalAccessor 时间对象
     * @return java.time.ZonedDateTime
     * @author yuanmengfan
     * @date 2022/3/26 11:48 下午
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor){
        if (temporalAccessor == null) {
            return null;
        }
        LocalDateTime result;
        if (temporalAccessor instanceof Instant) {
            result = LocalDateTime.ofInstant((Instant) temporalAccessor,ZoneId.systemDefault());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result =  ((ZonedDateTime) temporalAccessor).toLocalDateTime();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toLocalDateTime();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = (LocalDateTime) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDate) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((LocalDate) temporalAccessor).atStartOfDay();
        } else if (temporalAccessor instanceof LocalTime) {
            // 返回为纪元开始的时间 1970-1-1 LocalTime
            result = ((LocalTime) temporalAccessor).atDate(getEpochDate());
        } else if (temporalAccessor instanceof OffsetTime) {
            // 返回为当前日期的开始的时间也就是 当天的00:00:00.000
            result = ((OffsetTime) temporalAccessor).atDate(getEpochDate()).toLocalDateTime();
        } else {
            result = LocalDateTime.from(temporalAccessor);
        }
        return result;
    }

    /**
     * // 时间对象转换为 {@link java.util.Date}
     * @title toDate
     * @param temporalAccessor
     * @return java.util.Date
     * @author yuanmengfan
     * @date 2022/3/26 11:45 下午
     */
    public static Date toDate(TemporalAccessor temporalAccessor){
        return new Date(toInstant(temporalAccessor).toEpochMilli());
    }

    /**
     * // {@link java.util.Date} 转 ${@link LocalDateTime}
     * @title toLocalDateTime
     * @param date
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/27 12:03 上午
     */
    public static LocalDateTime toLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }
    /**
     * // {@link java.sql.Date} 转 ${@link LocalDateTime}
     * @title toLocalDateTime
     * @param date
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/27 12:03 上午
     */
    public static LocalDateTime toLocalDateTime(java.sql.Date date){
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }
    /**
     * // {@link Calendar} 转 ${@link LocalDateTime}
     * @title toLocalDateTime
     * @param date
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/27 12:03 上午
     */
    public static LocalDateTime toLocalDateTime(Calendar date){
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }
    /**
     * // {@link Timestamp} 转 ${@link LocalDateTime}
     * @title toLocalDateTime
     * @param date
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/27 12:03 上午
     */
    public static LocalDateTime toLocalDateTime(Timestamp date){
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }

    /**
     * // 获取纪元开始的日期
     * @title getEpochDate
     * @return java.time.LocalDate
     * @author yuanmengfan
     * @date 2022/3/27 5:23 下午
     */
    public static LocalDate getEpochDate() {
        return LocalDate.ofEpochDay(0);
    }

    /**
     * // 获取纪元开始的时间
     * @title getEpochDateTime
     * @return java.time.LocalDateTime
     * @author yuanmengfan
     * @date 2022/3/27 5:23 下午
     */
    public static LocalDateTime getEpochDateTime() {
        return LocalDate.ofEpochDay(0).atStartOfDay();
    }
}
