package com.github.linarusakova.mytracker.ui.dashboard;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.github.linarusakova.mytracker.util.ChartRecordFromDB;
import com.github.linarusakova.mytracker.util.DBHelper;
import com.github.linarusakova.mytracker.util.DateManager;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DashboardViewModel extends AndroidViewModel {
    private final DBHelper dbHelper;
    private final SQLiteDatabase db;
    ArrayList<BarEntry> entries;
    DateManager dateManager;

    public Float getAverageWeightOnThisWeekValue() {
        try {
            return calculateAverageWeightOnWeekValue(getData(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Float getAverageWeightOneWeekAgoValue() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        try {
            barEntries = getData(-1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return calculateAverageWeightOnWeekValue(barEntries);
    }

    public DashboardViewModel(@NonNull Application application) throws ParseException {
        super(application);
        this.dbHelper = DBHelper.getInstance(application.getBaseContext());
        this.db = dbHelper.getWritableDatabase();
        this.dateManager = new DateManager();
        this.entries = getData();
    }

    public ArrayList<BarEntry> getData() throws ParseException {
        return getData(0);
    }

    public static Date getWeekStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }

    public static Date getWeekEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 6);// last day of week
        return cal.getTime();
    }

    public ArrayList<BarEntry> getData(int current) throws ParseException {
        /*
         * if param current == 0 method return entries from current week
         * if current <0 method return entries from previous week: "-1" return one week ago, "-2" return two week ago, etc
         * */
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Date firstDateOnThisWeek = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar myCal = Calendar.getInstance();
        myCal.clear();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Date now = new Date();
        Date weekStartDate = getWeekStartDate(now);
        Date weekEndDate = getWeekEndDate(weekStartDate);

        if (current == 0) {
            if (weekEndDate.after(now))
                weekEndDate = now;
        }

        firstDateOnThisWeek = new Date(weekStartDate.getTime() + (long) 7 * current * 24 * 60 * 60 * 1000);
        Date currentDate = new Date(weekEndDate.getTime() + (long) 7 * current * 24 * 60 * 60 * 1000);

        for (int i = 0; i < 7; i++) {
            barEntries.add(new BarEntry(i, null));
        }

        List<ChartRecordFromDB> chartListFromDB = dbHelper.getChartListBetweenTwoDate(dbHelper.getWritableDatabase(),
                dateManager.getLocalDateInStringSimpleFormat(firstDateOnThisWeek),
                dateManager.getLocalDateInStringSimpleFormat(currentDate));

        if (!chartListFromDB.isEmpty()) {
            for (int i = 0; i < chartListFromDB.size(); i++) {
                Calendar calendar1 = Calendar.getInstance();
                Date d = formatter.parse((chartListFromDB.get(i)).getDate());
                calendar1.clear();
                calendar1.setTime(d);
                for (BarEntry e : barEntries
                ) {
                    if (e.getX() == d.getDay()) {
                        e.setY(Float.parseFloat(chartListFromDB.get(i).getWeight()));
                    }
                }
            }
        }

        if (calendar.getFirstDayOfWeek() == 2 && !chartListFromDB.isEmpty()) {
            Collections.rotate(barEntries, -1);
            for (int i = 0; i < barEntries.size(); i++) {
                barEntries.get(i).setX(i);
            }
        }
        return barEntries;
    }

    private Float calculateAverageWeightOnWeekValue(ArrayList<BarEntry> entries) {
        Float f = 0f;
        AtomicInteger size = new AtomicInteger(0);
        AtomicReference<Float> weight = new AtomicReference<>(0f);
        if (!entries.isEmpty()) {
            entries.stream().forEach(entry -> {
                if (entry.getY() > 0) {
                    weight.set(Float.sum(weight.get(), entry.getY()));
                    size.getAndIncrement();
                }
            });
            if (size.get() != 0)
                f = (float) weight.get() / size.get();
        }
        return f;
    }
}