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
import androidx.navigation.fragment.NavHostFragment;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AddRecordFragment extends Fragment {
    private Spinner exerciseSpinner;
    private Button confirm;
    private String name;
    private String reps;
    private Spinner unitSpinner;
    private Spinner numberSpinner;
    private ArrayList<String> numbersForSpinner;
    private String newNumberOfReps;
    private String value1;
    private String value2;
    private String newExerciseName;
    private final ArrayList<String> arrayOfExercises = new ArrayList<>();

    public AddRecordFragment() {}
    public AddRecordFragment(String name, String reps) {
        this.name = name;
        this.reps = reps;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_a_record_layout, container, false);
        RecordsAdapter.ifBelow = true;
        exerciseSpinner = rootView.findViewById(R.id.exerciseInput);
        confirm = rootView.findViewById(R.id.confirmExercise);
        unitSpinner = rootView.findViewById(R.id.unit);
        numberSpinner = rootView.findViewById(R.id.numberS);

        numbersForSpinner = new ArrayList<>();
        for (int i = 1; i <= 100; i++)
            numbersForSpinner.add(String.valueOf(i));

        ArrayList<ArrayList<WorkoutModel>> workoutArrays = new ArrayList<>();
        workoutArrays.add(HomeFragment.absArray);
        workoutArrays.add(HomeFragment.chestArray);
        workoutArrays.add(HomeFragment.backArray);
        workoutArrays.add(HomeFragment.shouldersArray);
        workoutArrays.add(HomeFragment.armsArray);
        workoutArrays.add(HomeFragment.legsArray);
        workoutArrays.add(HomeFragment.customArray);

        for (ArrayList<WorkoutModel> workoutArray : workoutArrays) {
            for (WorkoutModel workout : workoutArray)
                arrayOfExercises.add(workout.getName());
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> units = new ArrayList<>(Arrays.asList(getString(R.string.reps), getString(R.string.seconds), "kg"));

        Functions.setUpSpinner2(requireContext(), unitSpinner, units);
        Functions.setUpSpinner2(requireContext(), numberSpinner, numbersForSpinner);
        ArrayAdapter<String> adapterExercises = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayOfExercises);
        adapterExercises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapterExercises);

        if (name != null && reps != null)
        {
            int position = adapterExercises.getPosition(name);
            exerciseSpinner.setSelection(position);
            exerciseSpinner.setEnabled(false);
            exerciseSpinner.setFocusable(false);
            exerciseSpinner.setFocusableInTouchMode(false);
            String[] parts = reps.split(" ");
            String number = parts[0];
            String unit = parts[1];
            if(Objects.equals(unit, "s"))
            {
                unit = getString(R.string.seconds);
            }
            setSpinnerValue(numberSpinner, number);
            setSpinnerValue(unitSpinner, unit);
        }

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value2 = parent.getItemAtPosition(position).toString();
                if(value2.equals("powtórzenia"))
                    value2 = "reps";
                else if(value2.equals("sekundy"))
                    value2 = "seconds";
                if(value2.equals("seconds") )
                    value2 = "s";
                newNumberOfReps = value1 + " " + value2;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value1 = parent.getItemAtPosition(position).toString();
                newNumberOfReps = value1 + " " + value2;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newExerciseName = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        confirm.setOnClickListener(v -> {
            boolean exerciseExists = false;
            for (RecordModel recordModel : StatisticsFragment.recordsArray) {
                if (recordModel.getName().equals(newExerciseName)) {
                    recordModel.setNumber(newNumberOfReps);
                    exerciseExists = true;
                    break;
                }
            }
            if (!exerciseExists)
                StatisticsFragment.recordsArray.add(new RecordModel(newExerciseName, newNumberOfReps));
            FileUtility.saveRecords(requireContext(), "personalrecords.json", StatisticsFragment.recordsArray);
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            int currentId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
            int targetId = R.id.navigation_statistics;
            if (navController.getCurrentDestination() != null && currentId == targetId)
                navController.popBackStack();
            navController.navigate(targetId);
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
}
