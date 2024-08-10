package com.github.linarusakova.mytracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChartRecordFromDB {
    public String getDate() {
        return date;
    }

    public String getWeight() {
        return weight;
    }

    private String date;
    private String weight;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    private int dayOfWeek;

    public ChartRecordFromDB() {
    }

    public ChartRecordFromDB(String date, String weight) {
        this.date = date;
        this.dayOfWeek = calculateDay(date);
        this.weight = weight;
    }

    private int calculateDay(String date) {
        int day = 0;
        Date d;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        cal.clear();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public String toString() {
        return "OneDayRecord{" +
                "date='" + date + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}
