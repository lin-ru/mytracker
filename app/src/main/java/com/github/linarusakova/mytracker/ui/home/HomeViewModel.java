package com.github.linarusakova.mytracker.ui.home;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.linarusakova.mytracker.util.DBHelper;
import com.github.linarusakova.mytracker.util.OneDayRecord;

public class HomeViewModel extends AndroidViewModel {


//    private final MutableLiveData<String> mText;
    private final DBHelper dbHelper;
    private final SQLiteDatabase db;

    public HomeViewModel(@NonNull Application application) {
        super(application);

//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
        dbHelper = DBHelper.getInstance(application.getBaseContext());
        db = dbHelper.getWritableDatabase();

    }

//    public LiveData<String> getText() {
//        return mText;
//    }
    public void saveCurrentDay(OneDayRecord record) {
        dbHelper.insertDBRecord(db, record);
    }
    public OneDayRecord getRecordFromDBByDateString(String date) {
        return dbHelper.getOneRecordByDate(db, date);
    }

}