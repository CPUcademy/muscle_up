package com.muscleup.muscleup.ui.statistics;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StatsFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.today_stats, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.statsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        StatisticsFragment.personal_records.setClickable(false);
        StatisticsFragment.goals.setClickable(false);
        StatisticsFragment.today_stats.setClickable(false);

        if (isFileModifiedToday(requireContext(), "todaystats.json"))
            StatisticsFragment.statsArray = FileUtility.readTodayStats(requireContext(), "todaystats.json");
        else
            StatisticsFragment.statsArray = new ArrayList<>();

        StatsAdapter adapter = new StatsAdapter(StatisticsFragment.statsArray, getParentFragmentManager());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public static boolean isFileModifiedToday(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists())
            return false;
        long lastModified = file.lastModified();
        Date lastModifiedDate = new Date(lastModified);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String lastModifiedDateString = sdf.format(lastModifiedDate);
        String currentDateString = sdf.format(new Date());
        return lastModifiedDateString.equals(currentDateString);
    }
}
