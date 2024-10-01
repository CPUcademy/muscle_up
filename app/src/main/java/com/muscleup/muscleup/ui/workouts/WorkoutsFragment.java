package com.muscleup.muscleup.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.R;
import com.muscleup.muscleup.databinding.FragmentWorkoutsBinding;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutsFragment extends Fragment
{
    private FragmentWorkoutsBinding binding;
    public static int[] buttonIds;
    private LinearLayout[] buttons;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeFragment.restFailsafe = true;

        buttonIds = new int[]{R.id.abs_workout, R.id.chest_workout, R.id.back_workout, R.id.shoulders_workout, R.id.arms_workout, R.id.legs_workout, R.id.custom_workout, R.id.skills};
        buttons = new LinearLayout[buttonIds.length];

        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = root.findViewById(buttonIds[i]);
            final int finalI = i;
            buttons[i].setOnClickListener(v -> {
                switch (finalI) {
                    case 0:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("abs"), "exercises").commit();
                        disable();
                        break;
                    case 1:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("chest"), "exercises").commit();
                        disable();
                        break;
                    case 2:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("back"), "exercises").commit();
                        disable();
                        break;
                    case 3:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("shoulders"), "exercises").commit();
                        disable();
                        break;
                    case 4:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("arms"), "exercises").commit();
                        disable();
                        break;
                    case 5:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("legs"), "exercises").commit();
                        disable();
                        break;
                    case 6:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new WorkoutFragment("custom"), "exercises").commit();
                        disable();
                        break;
                    case 7:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag, new SkillsFragment(), "exercises").commit();
                        disable();
                        break;
                    default:
                        break;
                }
            });
        }

        return root;
    }

    public static HashMap<String, ArrayList<ArrayList<Object>>> convertToJsonStructure() {
        HashMap<String, ArrayList<ArrayList<Object>>> jsonStructure = new HashMap<>();
        jsonStructure.put("abs", convertArrayListToJsonStructure(HomeFragment.absArray));
        jsonStructure.put("chest", convertArrayListToJsonStructure(HomeFragment.chestArray));
        jsonStructure.put("back", convertArrayListToJsonStructure(HomeFragment.backArray));
        jsonStructure.put("shoulders", convertArrayListToJsonStructure(HomeFragment.shouldersArray));
        jsonStructure.put("arms", convertArrayListToJsonStructure(HomeFragment.armsArray));
        jsonStructure.put("legs", convertArrayListToJsonStructure(HomeFragment.legsArray));
        jsonStructure.put("custom", convertArrayListToJsonStructure(HomeFragment.customArray));
        return jsonStructure;
    }

    private static ArrayList<ArrayList<Object>> convertArrayListToJsonStructure(ArrayList<WorkoutModel> workoutArray) {
        ArrayList<ArrayList<Object>> list = new ArrayList<>();
        for (WorkoutModel workout : workoutArray) {
            ArrayList<Object> item = new ArrayList<>();
            item.add(workout.getName());
            item.add(workout.getReps());
            item.add(workout.getSets());
            item.add(workout.getWeight());
            item.add(workout.getDifficulty());
            list.add(item);
        }
        return list;
    }

    private void disable()
    {
        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = requireActivity().findViewById(buttonIds[i]);
            buttons[i].setClickable(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}