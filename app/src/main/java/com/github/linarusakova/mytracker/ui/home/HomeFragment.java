package com.github.linarusakova.mytracker.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.linarusakova.mytracker.R;
import com.github.linarusakova.mytracker.databinding.FragmentHomeBinding;
import com.github.linarusakova.mytracker.ui.dashboard.DashboardViewModel;
import com.github.linarusakova.mytracker.util.OneDayRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment implements View.OnClickListener, CalendarView.OnDateChangeListener {
    private FragmentHomeBinding binding;
    Date currentDate;
    private TextView textViewDate;
    private TextView textViewWeight;
    private EditText editTextWeight;
    private CalendarView calendarView;
    private final String LOG_TAG = "Main Activity DB HELPER";
    private EditText editTextMedical;
    private EditText editTextDayDetails;
    private Button buttonSave;
    private ScrollView scrollView;
    DashboardViewModel dashboardViewModel;
    HomeViewModel homeViewModel;
    boolean flag = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        textViewDate = binding.textViewCurrentDay;
        textViewWeight = binding.labelWeight;
        editTextWeight = binding.editTextWeight;
        calendarView = binding.calendarView;
        editTextMedical = binding.editTextTodayMedical;
        editTextDayDetails = binding.editTextTodayDetails;
        buttonSave = binding.buttonSave;
        scrollView = binding.scrolvw;
        calendarView.setVisibility(View.GONE);
        textViewDate.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        calendarView.setOnDateChangeListener(this);
        currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
        String timeCurrent = timeFormat.format(currentDate);

        textViewDate.setText(timeCurrent);
        textViewDate.setOnClickListener(this);
        scrollView.setOnClickListener(this);
        editTextWeight.requestFocus();

        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        DateFormat timeFormatForDB = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDateDBStr = timeFormatForDB.format(currentDate);
        getAndSetDataFromDB(selectedDateDBStr);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    Handler handler = new Handler();
    @Override
    public void onClick(View v) {
        if (v == textViewDate) {
            if (flag) {
                calendarView.setVisibility(View.VISIBLE);
            } else {
                calendarView.setVisibility(View.GONE);
            }
            flag = !flag;
        } else if (v == buttonSave) {
            CharSequence text;
            if (editTextWeight.getText().length() != 0) {
                Date date;
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    date = format.parse(textViewDate.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                String timeCurrentDB = timeFormat.format(date);
                OneDayRecord record = new OneDayRecord(timeCurrentDB, String.valueOf(editTextWeight.getText()), String.valueOf(editTextMedical.getText()), String.valueOf(editTextDayDetails.getText()));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        homeViewModel.saveCurrentDay(record);
                        try {
                            dashboardViewModel.getData(0);
                            dashboardViewModel.getData(-1);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(LOG_TAG, "--- insert Record to DB:" + record.toString());
                    }
                });
                text = getResources().getString(com.github.linarusakova.mytracker.R.string.toastTextSaveCurrentDay);
            } else {
                text = getResources().getString(com.github.linarusakova.mytracker.R.string.toastTextNoSaveZeroWeight);
            }
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getContext(), text, duration);
            toast.show();
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Calendar toDate = Calendar.getInstance();
        Calendar nowDate = Calendar.getInstance();
        toDate.set(year, month, dayOfMonth);
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
        String selectedDate = timeFormat.format(toDate.getTime());
        DateFormat timeFormatDB = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDateDB = timeFormatDB.format(toDate.getTime());

        if (!toDate.after(nowDate)) {
            calendarView.setVisibility(View.GONE);
            editTextWeight.clearFocus();
            textViewDate.setText(selectedDate);
            getAndSetDataFromDB(selectedDateDB);
        } else {
            editTextWeight.clearFocus();
            editTextWeight.requestFocus();
            CharSequence text = getResources().getString(R.string.toastTextSetDayError);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getContext(), text, duration);
            toast.show();
        }
    }

    private void getAndSetDataFromDB(String selectedDateDB) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                OneDayRecord dayRecord = homeViewModel.getRecordFromDBByDateString(selectedDateDB); // search one Record by this day
                Log.d(LOG_TAG, "--- SEARCH one Record by Date to DB:" + dayRecord.toString());
                editTextWeight.setText((dayRecord.getWeight() != null) ? dayRecord.getWeight() : "");
                editTextMedical.setText((dayRecord.getMedical() != null) ? dayRecord.getMedical() : "");
                editTextDayDetails.setText((dayRecord.getDayDetails() != null) ? dayRecord.getDayDetails() : "");
                editTextWeight.requestFocus();
            }
        });
    }
}