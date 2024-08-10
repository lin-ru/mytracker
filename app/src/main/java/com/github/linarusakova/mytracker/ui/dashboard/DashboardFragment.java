package com.github.linarusakova.mytracker.ui.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidplot.xy.XYPlot;
import com.github.linarusakova.mytracker.R;
import com.github.linarusakova.mytracker.databinding.FragmentDashboardBinding;
import com.github.linarusakova.mytracker.util.DateManager;
import com.github.linarusakova.mytracker.util.OneDayRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    TextView textViewValueAverageWeight;
    TextView textViewValueAverageWeightWeekAgo;
    private final String LOG_TAG = "Dashboard DB HELPER";
    Context context;
    DashboardViewModel dashboardViewModel;

    BarChart chart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> liveBarEntries;

    private char stableAverageUnit = '\u25ba';

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = root.getContext();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);

        int colorAxis = getContext().getColor(R.color.textColorXaxis);
        int colorBarChart = getContext().getColor(R.color.colorBarChart);

        Description description = new Description();
        description.setText(getResources().getString(R.string.descritpion_of_current_week_chart));
        description.setTextColor(colorAxis);
        description.setTextSize(16);
        description.setTextAlign(Paint.Align.LEFT);
        description.setPosition(10f, 40f);
        textViewValueAverageWeight = binding.textViewValueAverageWeight;
        textViewValueAverageWeightWeekAgo = binding.textViewValueAverageWeightWeekAgo;

        List<String> daysOfWeek = Arrays.asList(getResources().getStringArray(R.array.day_of_week_array));

        String unitsWeight = getResources().getString(R.string.chart_value_weight_unit);
        chart = binding.barChart;
        chart.setExtraTopOffset(40f);
        chart.setExtraBottomOffset(10f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(20);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(colorAxis);
        yAxis.setAxisLineColor(colorAxis);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        try {
            barEntries = dashboardViewModel.getData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        barDataSet = new BarDataSet(barEntries, getResources().getString(R.string.chart_weight_legend));
        barDataSet.setColor(colorBarChart);
        chart.getLegend().setTextColor(colorAxis);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        Float averageWeightOnThisWeek = dashboardViewModel.getAverageWeightOnThisWeekValue();
        Float averageWeightOnOneWeekAgo = dashboardViewModel.getAverageWeightOneWeekAgoValue();
        if (averageWeightOnThisWeek > averageWeightOnOneWeekAgo) {
            textViewValueAverageWeight.setTextColor(Color.RED);
            stableAverageUnit = '▲';
        } else if (averageWeightOnThisWeek < averageWeightOnOneWeekAgo) {
            textViewValueAverageWeight.setTextColor(Color.GREEN);
            stableAverageUnit = '▼';
        }
        String averageWeightStr = decimalFormat.format(averageWeightOnThisWeek) + "" + stableAverageUnit;
        String averageWeightAgoStr = decimalFormat.format(averageWeightOnOneWeekAgo);
        textViewValueAverageWeight.setText(averageWeightStr);
        textViewValueAverageWeightWeekAgo.setText(averageWeightAgoStr);
        barDataSet.setValueTextColor(colorBarChart);
        barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return (String) (barEntry.getY() + unitsWeight);

            }
        });
        barData.setValueTextColor(colorAxis);
        chart.clear();
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setAxisLineColor(colorAxis);


        yAxis.setDrawLabels(true);
        yAxis.setTextColor(colorAxis);
        yAxis.setDrawAxisLine(true);

        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(colorAxis);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);

        barDataSet.setVisible(true);
        barDataSet.setValueTextSize(12f);

        chart.getXAxis().setLabelCount(7);
        chart.setDescription(description);
        chart.notifyDataSetChanged();
        chart.invalidate();
        return root;
    }

    @Override
    public void onDestroyView() {
        chart.notifyDataSetChanged();
        chart.invalidate();
        super.onDestroyView();
        binding = null;
    }
}