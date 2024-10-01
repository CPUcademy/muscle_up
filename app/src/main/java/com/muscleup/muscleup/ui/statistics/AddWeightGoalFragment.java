package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.util.ArrayList;

public class AddWeightGoalFragment extends Fragment {
    private Spinner currentWeight;
    private Spinner weightGoal;
    private Button confirm;
    private ArrayList<String> numbersForSpinner;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_a_weight_goal_layout, container, false);
        currentWeight = rootView.findViewById(R.id.currentWeight);
        weightGoal = rootView.findViewById(R.id.weightGoal);
        confirm = rootView.findViewById(R.id.confirmWeight);

        numbersForSpinner = new ArrayList<>();
        for (int i = 1; i <= 200; i++)
            numbersForSpinner.add(String.valueOf(i));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Functions.setUpSpinner2(requireContext(), currentWeight, numbersForSpinner);
        Functions.setUpSpinner2(requireContext(), weightGoal, numbersForSpinner);
        setSpinnerValue(currentWeight, String.valueOf(WeightFragment.currentWeight));
        setSpinnerValue(weightGoal, String.valueOf(WeightFragment.weightGoal));

        weightGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WeightFragment.weightGoal = Integer.parseInt(parent.getItemAtPosition(position).toString());
                saveWeight();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        currentWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WeightFragment.currentWeight = Integer.parseInt(parent.getItemAtPosition(position).toString());
                saveWeight();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        confirm.setOnClickListener(v -> {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .setPopEnterAnim(android.R.anim.fade_in)
                        .setPopExitAnim(android.R.anim.fade_out)
                        .build();
                navController.navigate(R.id.navigation_statistics, null, navOptions);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stats, new WeightFragment(), "weight").commit();
                ArrayList<Integer> progressList = FileUtility.readStats(requireContext(), "weightProgress.json");
                progressList.add(WeightFragment.currentWeight);
                FileUtility.saveStats(requireContext(), "weightProgress.json", progressList);
            }
        });
    }

    public static void setSpinnerValue(Spinner spinner, String value)
    {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private void saveWeight()
    {
        HomeFragment.weight.set(0, WeightFragment.currentWeight);
        HomeFragment.weight.set(1, WeightFragment.weightGoal);
        FileUtility.saveStats(requireContext(), "weight.json", HomeFragment.weight);
    }
}
