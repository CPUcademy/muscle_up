package com.muscleup.muscleup.ui.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Objects;

public class WorkoutFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private Button title;
    @SuppressLint("StaticFieldLeak")
    public static Button addExercises;
    public static String group;
    private ArrayList<WorkoutModel> array = new ArrayList<WorkoutModel>();

    public WorkoutFragment(String group) {
        WorkoutFragment.group = group;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.training_layout, container, false);

        title = root.findViewById(R.id.title);

        array.clear();
        if (Objects.equals(group, "abs")) {
            array = HomeFragment.absArray;
            title.setText(R.string.abs_workout);
        } else if (Objects.equals(group, "chest")) {
            array = HomeFragment.chestArray;
            title.setText(R.string.chest_workout);
        } else if (Objects.equals(group, "back")) {
            array = HomeFragment.backArray;
            title.setText(R.string.back_workout);
        } else if (Objects.equals(group, "shoulders")) {
            array = HomeFragment.shouldersArray;
            title.setText(R.string.shoulders_workout);
        } else if (Objects.equals(group, "arms")) {
            array = HomeFragment.armsArray;
            title.setText(R.string.arms_workout);
        } else if (Objects.equals(group, "legs")) {
            array = HomeFragment.legsArray;
            title.setText(R.string.legs_workout);
        } else if (Objects.equals(group, "custom")) {
            array = HomeFragment.customArray;
            title.setText(R.string.custom_workout);
        }

        recyclerView = root.findViewById(R.id.exercisesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new WorkoutAdapter(array, getParentFragmentManager(), group, getContext());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        addExercises = root.findViewById(R.id.add_an_exercise);
        addExercises.setVisibility(View.VISIBLE);

        addExercises.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.exercisesLayout, new AddExerciseFragment(group), "exercises").commit();
            addExercises.setVisibility(View.GONE);
            addExercises.setClickable(false);
        });

        return root;
    }
}
