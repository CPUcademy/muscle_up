package com.muscleup.muscleup.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;
import com.muscleup.muscleup.ui.workouts.WorkoutsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class ListOfExercisesFragment extends Fragment {

    private RecyclerView recyclerView;
    private final ArrayList<ListModel> array = new ArrayList<ListModel>();
    private String difficulty = "All", supported = "All", group = "All", equipment = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_of_exercises_layout, container, false);
        HomeFragment.personal_plan.setClickable(false);
        HomeFragment.start_exercising.setClickable(false);
        HomeFragment.listOfExercises.setClickable(false);

        if (WorkoutsFragment.buttonIds != null) {
            for (int buttonId : WorkoutsFragment.buttonIds) {
                Button button = requireActivity().findViewById(buttonId);
                if (button != null)
                    button.setEnabled(false);
            }
        }

        Spinner difficultySpinner = root.findViewById(R.id.difficulty_spinner);
        Functions.setUpSpinner(requireContext(), difficultySpinner, R.array.difficulty_levels, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficulty = parent.getItemAtPosition(position).toString().toLowerCase();
                difficulty = Functions.translateDifficulty(difficulty);
                fillTheArray();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner supportedSpinner = root.findViewById(R.id.supported_spinner);
        Functions.setUpSpinner(requireContext(), supportedSpinner, R.array.support_spinner_levels, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                supported = parent.getItemAtPosition(position).toString();
                fillTheArray();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner groupSpinner = root.findViewById(R.id.group_spinner);
        Functions.setUpSpinner(requireContext(), groupSpinner, R.array.group_spinner_levels, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = parent.getItemAtPosition(position).toString().toLowerCase();
                if(group.equals("biceps & triceps"))
                    group = getString(R.string.armsS);
                fillTheArray();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner equipmentSpinner = root.findViewById(R.id.equipment_spinner);
        Functions.setUpSpinner(requireContext(), equipmentSpinner, R.array.support_spinner_levels, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equipment = parent.getItemAtPosition(position).toString();
                fillTheArray();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        recyclerView = root.findViewById(R.id.listOfExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fillTheArray();

        return root;
    }

    private void fillTheArray()
    {
        array.clear();
        ArrayList<ArrayList<WorkoutModel>> workoutArrays = new ArrayList<>();
        workoutArrays.add(HomeFragment.absArrayList);
        workoutArrays.add(HomeFragment.chestArrayList);
        workoutArrays.add(HomeFragment.backArrayList);
        workoutArrays.add(HomeFragment.shouldersArrayList);
        workoutArrays.add(HomeFragment.armsArrayList);
        workoutArrays.add(HomeFragment.legsArrayList);

        ArrayList<String> groups = new ArrayList<>(Arrays.asList(getString(R.string.absS), getString(R.string.chestS), getString(R.string.backS), getString(R.string.shouldersS), getString(R.string.armsS), getString(R.string.legsS), getString(R.string.custom)));
        int c = 0;
        for (ArrayList<WorkoutModel> workoutArray : workoutArrays)
        {
            for (WorkoutModel workout : workoutArray)
            {
                String name = workout.getName();
                if((Objects.equals(equipment, getString(R.string.no)) && (HomeFragment.dumbbellList.contains(name) || HomeFragment.pullupList.contains(name) || HomeFragment.dipList.contains(name))) ||
                        (Objects.equals(equipment, getString(R.string.yes)) && !(HomeFragment.dumbbellList.contains(name) || HomeFragment.pullupList.contains(name) || HomeFragment.dipList.contains(name)))
                        || (!Objects.equals(group, getString(R.string.all)) && !Objects.equals(groups.get(c), group)) ||
                        (!Objects.equals(difficulty, getString(R.string.all)) && !Objects.equals(Functions.parseDifficulty(workout.getDifficulty()), difficulty)) ||
                        (Objects.equals(supported, getString(R.string.yes)) && !HomeFragment.supportedExercises.contains(name)) ||
                        (Objects.equals(supported, getString(R.string.no)) && HomeFragment.supportedExercises.contains(name)))
                    continue;
                String diff = Functions.parseDifficulty(workout.getDifficulty());
                if(Locale.getDefault().getLanguage().equals("pl"))
                    if(diff.equals("easy"))
                        diff = "łatwy";
                    else if(diff.equals("medium"))
                        diff = "średni";
                    else if(diff.equals("hard"))
                        diff = "trudny";
                array.add(new ListModel(workout.getName(), diff, groups.get(c)));
            }
            c++;
        }

        ListAdapter adapter = new ListAdapter(array, getParentFragmentManager());
        recyclerView.setAdapter(adapter);
    }
}
