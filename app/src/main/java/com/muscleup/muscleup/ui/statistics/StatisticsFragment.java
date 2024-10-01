package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.databinding.FragmentStatisticsBinding;
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.home.SessionModel;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment
{
    private FragmentStatisticsBinding binding;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout personal_records;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout goals;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout today_stats;
    public static ArrayList<RecordModel> recordsArray;
    public static ArrayList<SessionModel> statsArray = new ArrayList<>();
    public static LinearLayout other_stats;
    private LineChart lineChart;
    private List<String> xValues = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeFragment.restFailsafe = true;

        FileUtility.copyFileToInternalStorage(requireContext(), "personalrecords.json");
        recordsArray = FileUtility.readRecords(requireContext(), "personalrecords.json");

        HomeFragment.formWidgetValues = FileUtility.readStats(requireContext(), "form.json");
        if(HomeFragment.formWidgetValues.size() >= 7)
        {
            double average = calculateAverage(HomeFragment.formWidgetValues);
            int lastValue = HomeFragment.formWidgetValues.get(HomeFragment.formWidgetValues.size() - 1);
            ArrayList<Integer> graphList = FileUtility.readStats(requireContext(), "graphFormData.json");
            graphList.add((int) Math.round(average));
            FileUtility.saveStats(requireContext(), "graphFormData.json", graphList);

            HomeFragment.formWidgetValues = new ArrayList<>();
            HomeFragment.formWidgetValues.add(lastValue);
            FileUtility.saveStats(requireContext(), "form.json", HomeFragment.formWidgetValues);
        }

        personal_records = root.findViewById(R.id.personal_records);
        personal_records.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new RecordFragment(), "records").commit());

        goals = root.findViewById(R.id.goals);
        goals.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new WeightFragment(), "weight").commit());

        today_stats = root.findViewById(R.id.today_stats);
        today_stats.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new StatsFragment(), "today_stats").commit());

        other_stats = root.findViewById(R.id.other_stats);
        other_stats.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new OtherStats(), "other_stats").commit());

        lineChart = root.findViewById(R.id.chart);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);

        ArrayList<Integer> graphList = FileUtility.readStats(requireContext(), "graphFormData.json");
        for(int i = 1; i < graphList.size()+1; i++)
            xValues.add(String.valueOf(i));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setLabelCount(10);

        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < graphList.size(); i++)
            entries.add(new Entry(i, graphList.get(i)));

        LineDataSet dataSet = new LineDataSet(entries, "Stats");
        dataSet.setColor(Color.rgb(189, 48, 80));
        dataSet.setCircleColor(Color.rgb(189, 48, 80));
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        return root;
    }

    public static double calculateAverage(ArrayList<Integer> list) {
        int sum = 0;
        for (int value : list)
            sum += value;
        return (double) sum / list.size();
    }

    public static void saveFormValue(Context context, int form) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        long currentTimeMillis = System.currentTimeMillis();
        editor.putInt("last_form_value", form);
        editor.putLong("last_form_timestamp", currentTimeMillis);
        editor.apply();

        HomeFragment.formWidgetValues.add(form);
        FileUtility.saveStats(context, "stats.json", HomeFragment.homeWidgetValues);
        FileUtility.saveStats(context, "form.json", HomeFragment.formWidgetValues);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}