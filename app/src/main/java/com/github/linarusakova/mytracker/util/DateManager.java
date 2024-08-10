package com.github.linarusakova.mytracker.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateManager {
    public Date getCurrentDate() {
        return new Date();
    }

    public Date getCurrentDayOnThisWeek() {
        Calendar myCal = new GregorianCalendar();
        myCal.setTime(getCurrentDate());
        int firstDay = myCal.get(Calendar.DAY_OF_WEEK);
        Date date;
        myCal.add(Calendar.YEAR, -firstDay);
        return date = myCal.getTime();
    }

    public String getLocalDateInStringSimpleFormat(Date date) {
        DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        return timeFormat.format(date);
    }

    public Integer getDayFromDate(String stringDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(stringDate);
        Calendar myCal = new GregorianCalendar();
        myCal.setTime(date);
        return myCal.get(Calendar.DAY_OF_MONTH);
    }

    public String getDayByDate(String stringDate) {
        Calendar myCal = new GregorianCalendar();
        myCal.setTime(getCurrentDate());
        Integer firstDay = myCal.get(Calendar.DAY_OF_WEEK);
        Date date;
        myCal.add(Calendar.YEAR, -firstDay);
        date = myCal.getTime();
        DateFormat f = new SimpleDateFormat("EEEE");
        return f.format(date);
    }

    public Date getDateFromDateString(String stringDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(stringDate);
    }
}
