package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class WeightFragment extends Fragment
{
    public static int currentWeight = 60;
    public static int weightGoal = 60;
    private LineChart lineChart;
    private List<String> xValues = new ArrayList<>();

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.weight_goal_layout, container, false);
        StatisticsFragment.personal_records.setClickable(false);
        StatisticsFragment.goals.setClickable(false);
        StatisticsFragment.today_stats.setClickable(false);
        StatisticsFragment.other_stats.setClickable(false);
        currentWeight = HomeFragment.weight.get(0);
        weightGoal = HomeFragment.weight.get(1);

        TextView weight_goal = root.findViewById(R.id.weight_goal);
        SpannableString spannableString = new SpannableString(getString(R.string.your_weight_goal));
        RelativeSizeSpan smallerTextSize = new RelativeSizeSpan(0.9f);
        spannableString.setSpan(smallerTextSize, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan = new SpannableString(weightGoal + " kg");
        numberSpan.setSpan(new RelativeSizeSpan(2), 0, numberSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText = TextUtils.concat(spannableString, "\n", numberSpan);
        weight_goal.setText(finalText);

        TextView current_weight = root.findViewById(R.id.weight_now);
        SpannableString spannableString2 = new SpannableString(getString(R.string.your_current_weight));
        RelativeSizeSpan smallerTextSize2 = new RelativeSizeSpan(0.9f);
        spannableString2.setSpan(smallerTextSize2, 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan2 = new SpannableString(currentWeight + " kg");
        numberSpan2.setSpan(new RelativeSizeSpan(2), 0, numberSpan2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText2 = TextUtils.concat(spannableString2, "\n", numberSpan2);
        current_weight.setText(finalText2);

        if(findProgress() == 100) {
            current_weight.setBackground(getResources().getDrawable(R.drawable.rounded_corners_pink_stroke));
            weight_goal.setBackground(getResources().getDrawable(R.drawable.rounded_corners_pink_stroke));
        }
        
        Button edit = root.findViewById(R.id.editWeight);
        edit.setOnClickListener(v ->
        {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new AddWeightGoalFragment(), "weight").addToBackStack(null).commit();
            setGraph();
        });

        lineChart = root.findViewById(R.id.chart);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);
        setGraph();

        return root;
    }

    public void setGraph()
    {
        ArrayList<Integer> graphList = FileUtility.readStats(requireContext(), "weightProgress.json");
        for(int i = 1; i < graphList.size()+1; i++)
            xValues.add(String.valueOf(i));

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(currentWeight+80);
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
    }

    public static int findProgress()
    {
        float progres = 0.0F;
        if(weightGoal < currentWeight)
            progres = ((float) weightGoal / currentWeight);
        else
            progres = ((float) currentWeight / weightGoal);
        int progress = (int) (progres * 100);

        return progress;
    }
}