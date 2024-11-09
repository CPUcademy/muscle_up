package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.R;

public class RecordFragment extends Fragment
{
    @SuppressLint("StaticFieldLeak")
    public static Button addExercises;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_layout, container, false);
        StatisticsFragment.personal_records.setClickable(false);
        StatisticsFragment.goals.setClickable(false);
        StatisticsFragment.today_stats.setClickable(false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recordsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        RecordsAdapter adapter = new RecordsAdapter(StatisticsFragment.recordsArray, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        addExercises = rootView.findViewById(R.id.add_a_record);
        addExercises.setVisibility(View.VISIBLE);

        addExercises.setOnClickListener(v ->
        {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recordsLayout, new AddRecordFragment(), "records").commit();
            addExercises.setVisibility(View.GONE);
            addExercises.setClickable(false);
        });

        return rootView;
    }
}
