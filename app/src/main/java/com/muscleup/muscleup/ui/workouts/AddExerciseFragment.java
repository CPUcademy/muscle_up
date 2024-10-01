package com.muscleup.muscleup.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.muscleup.muscleup.ui.statistics.AddRecordFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AddExerciseFragment extends Fragment
{
    private EditText exercise;
    private final String group;
    private String name = "plank";
    private Integer reps;
    private Integer sets;
    private Integer weightt;
    private String diffic;
    private String newNumberOfReps;
    private String newNumberOfSets;
    private String newWeight;
    private String newDifficulty;
    private String newUnit;
    private ArrayList<String> numbersForSpinner;
    private ArrayList<WorkoutModel> array;

    public AddExerciseFragment(String group){this.group = group;}

    public AddExerciseFragment(String group, String name, Integer reps, Integer sets, Integer weightt, String diffic)
    {
        this.group = group;
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.weightt = weightt;
        this.diffic = diffic;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_an_exercise_layout, container, false);

        WorkoutAdapter.ifBelow = true;

        exercise = root.findViewById(R.id.exerciseInput);
        Spinner numberOfSets = root.findViewById(R.id.numberOfSets);
        Spinner numberOfReps = root.findViewById(R.id.numberOfReps);
        Spinner difficultySpinner = root.findViewById(R.id.difficulty_sp);
        Spinner weight = root.findViewById(R.id.weight);
        Spinner unit = root.findViewById(R.id.unit);
        Button confirm = root.findViewById(R.id.confirmExercise);

        if(Objects.equals(group, "abs"))
            array = HomeFragment.absArray;
        else if(Objects.equals(group, "chest"))
            array = HomeFragment.chestArray;
        else if(Objects.equals(group, "back"))
            array = HomeFragment.backArray;
        else if(Objects.equals(group, "shoulders"))
            array = HomeFragment.shouldersArray;
        else if(Objects.equals(group, "arms"))
            array = HomeFragment.armsArray;
        else if(Objects.equals(group, "legs"))
            array = HomeFragment.legsArray;
        else if(Objects.equals(group, "custom"))
            array = HomeFragment.customArray;

        numbersForSpinner = new ArrayList<>();
        fillNumbersForSpinner(50);
        Functions.setUpSpinner2(requireContext(), numberOfReps, numbersForSpinner);
        fillNumbersForSpinner(500);
        Functions.setUpSpinner2(requireContext(), weight, numbersForSpinner);

        Functions.setUpSpinner(requireContext(), unit, R.array.unit_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newUnit = parent.getItemAtPosition(position).toString();
                if(newUnit.equals("powtórzenia"))
                    newUnit = "reps";
                else if(newUnit.equals("sekundy"))
                    newUnit = "seconds";
                if(newUnit.equals("seconds") && !HomeFragment.arrayOfSeconds.contains(Functions.stripe(name))) {
                    String stripedName = Functions.stripe(name);
                    if (!HomeFragment.arrayOfSeconds.contains(stripedName))
                        HomeFragment.arrayOfSeconds.add(stripedName);
                    FileUtility.saveSettings(requireContext(), "secondsExercises.json", HomeFragment.arrayOfSeconds);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.difficulty_levels2, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(spinnerAdapter);
        numbersForSpinner = new ArrayList<>();
        fillNumbersForSpinner(10);
        Functions.setUpSpinner2(requireContext(), numberOfSets, numbersForSpinner);

        if(name != null && reps != null && sets != null && weightt != null && diffic != null)
        {
            exercise.setText(name);
            exercise.setFocusable(false);
            AddRecordFragment.setSpinnerValue(numberOfReps, String.valueOf(reps));
            AddRecordFragment.setSpinnerValue(numberOfSets, String.valueOf(sets));
            AddRecordFragment.setSpinnerValue(weight, String.valueOf(weightt));
            AddRecordFragment.setSpinnerValue(difficultySpinner, String.valueOf(diffic));
            if(HomeFragment.arrayOfSeconds.contains(name)) {
                if (Locale.getDefault().getLanguage().equals("pl"))
                    AddRecordFragment.setSpinnerValue(unit, "sekundy");
                else
                    AddRecordFragment.setSpinnerValue(unit, "seconds");
            }
        }

        numberOfReps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newNumberOfReps = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        numberOfSets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newNumberOfSets = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newWeight = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newDifficulty = parent.getItemAtPosition(position).toString();
                newDifficulty = Functions.translateDifficulty(newDifficulty);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        confirm.setOnClickListener(v ->
        {
            String newExerciseName = exercise.getText().toString();

            boolean exerciseExists = false;
            for (WorkoutModel workoutModel : array)
            {
                if (workoutModel.getName().equals(newExerciseName))
                {
                    workoutModel.setReps(Integer.parseInt(newNumberOfReps));
                    workoutModel.setSets(Integer.parseInt(newNumberOfSets));
                    workoutModel.setWeight(Integer.parseInt(newWeight));
                    workoutModel.setDifficulty(Functions.parseDifficultyRev(newDifficulty));
                    exerciseExists = true;
                    break;
                }
            }

            if (!exerciseExists)
            {
                array.add(new WorkoutModel(newExerciseName, Integer.parseInt(newNumberOfReps), Integer.parseInt(newNumberOfSets), Integer.parseInt(newWeight), Functions.parseDifficultyRev(newDifficulty)));
            }

            HomeFragment.exercisesMap = WorkoutsFragment.convertToJsonStructure();
            FileUtility.saveWorkouts(requireContext(), HomeFragment.exercisesMap, "exercises.json");

            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            int currentId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
            int targetId = R.id.navigation_workouts;
            if (navController.getCurrentDestination() != null && currentId == targetId)
                navController.popBackStack();
            navController.navigate(targetId);
        });

        return root;
    }

    private void fillNumbersForSpinner(int range)
    {
        numbersForSpinner.clear();
        for (int i = 1; i <= range; i++)
            numbersForSpinner.add(String.valueOf(i));
    }
}
