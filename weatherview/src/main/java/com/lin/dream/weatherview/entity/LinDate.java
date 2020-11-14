package com.lin.dream.weatherview.entity;

import java.util.Calendar;
import java.util.Objects;

/**
 * <p> Title: Day </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/30/20.
 * @date 10/30/20
 */

public class LinDate {

    final static long ONE_DAY = 24 * 60 * 60 * 1000;

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public long timeStamp;

    public LinDate(long timeStamp) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timeStamp);
        this.year = instance.get(Calendar.YEAR);
        this.month = instance.get(Calendar.MONTH) + 1;
        this.day = instance.get(Calendar.DAY_OF_MONTH);
        this.hour = instance.get(Calendar.HOUR_OF_DAY);
        this.minute = instance.get(Calendar.MINUTE);
        this.second = instance.get(Calendar.SECOND);
        this.timeStamp = timeStamp;
    }

    public LinDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        Calendar instance = Calendar.getInstance();
        instance.set(year, month, day, 0, 0, 0);
        this.timeStamp = instance.getTimeInMillis();
    }

    public LinDate(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public String getWeekStr() {
        if (isToday()) return "今天";
        if (isYesterday()) return "昨天";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (weekDay) {
            case Calendar.SUNDAY:
                weekStr = "周日";
                break;
            case Calendar.MONDAY:
                weekStr = "周一";
                break;
            case Calendar.TUESDAY:
                weekStr = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekStr = "周三";
                break;
            case Calendar.THURSDAY:
                weekStr = "周四";
                break;
            case Calendar.FRIDAY:
                weekStr = "周五";
                break;
            case Calendar.SATURDAY:
                weekStr = "周六";
                break;
        }
        return weekStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinDate date = (LinDate) o;
        return year == date.year &&
                month == date.month &&
                day == date.day &&
                hour == date.hour &&
                minute == date.minute &&
                second == date.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, hour, minute, second);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"year\":")
                .append(year);
        sb.append(",\"month\":")
                .append(month);
        sb.append(",\"day\":")
                .append(day);
        sb.append(",\"hour\":")
                .append(hour);
        sb.append(",\"minute\":")
                .append(minute);
        sb.append(",\"second\":")
                .append(second);
        sb.append(",\"timeStamp\":")
                .append(timeStamp);
        sb.append('}');
        return sb.toString();
    }

    public boolean isSameDay(LinDate date) {
        return isSameDay(date.year, date.month, date.day);
    }

    public boolean isSameDay(int year, int month, int day) {
        return this.year == year &&
                this.month == month &&
                this.day == day;
    }

    public boolean isToday() {
        Calendar today = Calendar.getInstance();
        return year == today.get(Calendar.YEAR) &&
                (month - 1) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH);
    }

    public boolean isYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(yesterday.getTimeInMillis() - ONE_DAY);
        return year == yesterday.get(Calendar.YEAR) &&
                (month - 1) == yesterday.get(Calendar.MONTH) &&
                day == yesterday.get(Calendar.DAY_OF_MONTH);
    }

    public boolean isCurrentHour() {
        Calendar today = Calendar.getInstance();
        return year == today.get(Calendar.YEAR) &&
                (month - 1) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH) &&
                hour == today.get(Calendar.HOUR_OF_DAY);
    }

}
