package com.muscleup.muscleup.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.settings.SettingsFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Objects;

public class PersonalPlanFragment extends Fragment
{
    public ArrayList<int[]> saveArray = new ArrayList();
    private CheckBox[][] checkBoxes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.personal_plan_layout, container, false);
        HomeFragment.personal_plan.setClickable(false);
        HomeFragment.start_exercising.setClickable(false);
        HomeFragment.listOfExercises.setClickable(false);

        String[] daysOfWeek = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        checkBoxes = new CheckBox[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                int resID = getResources().getIdentifier(HomeFragment.muscleGroups[j] + "_" + daysOfWeek[i], "id", getActivity().getPackageName());
                checkBoxes[i][j] = rootView.findViewById(resID);
            }
        }
        boolean[] daysPresent = new boolean[7];
        for(int i = 0; i < 7; i++)
            daysPresent[i] = SettingsFragment.arrayOfTrainingDays.get(i) == 1;

        for (int i = 0; i < 7; i++) {
            if (!daysPresent[i]) {
                int fragmentId = getResources().getIdentifier(daysOfWeek[i] + "Fragment", "id", requireActivity().getPackageName());
                requireActivity().getSupportFragmentManager().beginTransaction().replace(fragmentId, new NoTrainingFragment(), "no_training").commit();

                for (int j = 0; j < 7; j++) {
                    checkBoxes[i][j].setClickable(false);
                    switch (i) {
                        case 0:
                            HomeFragment.mondayArray = new int[HomeFragment.mondayArray.length];
                            break;
                        case 1:
                            HomeFragment.tuesdayArray = new int[HomeFragment.tuesdayArray.length];
                            break;
                        case 2:
                            HomeFragment.wednesdayArray = new int[HomeFragment.wednesdayArray.length];
                            break;
                        case 3:
                            HomeFragment.thursdayArray = new int[HomeFragment.thursdayArray.length];
                            break;
                        case 4:
                            HomeFragment.fridayArray = new int[HomeFragment.fridayArray.length];
                            break;
                        case 5:
                            HomeFragment.saturdayArray = new int[HomeFragment.saturdayArray.length];
                            break;
                        case 6:
                            HomeFragment.sundayArray = new int[HomeFragment.sundayArray.length];
                            break;
                    }
                }
            }
        }

        int[][] arrays = new int[7][];
        arrays[0] = HomeFragment.mondayArray;
        arrays[1] = HomeFragment.tuesdayArray;
        arrays[2] = HomeFragment.wednesdayArray;
        arrays[3] = HomeFragment.thursdayArray;
        arrays[4] = HomeFragment.fridayArray;
        arrays[5] = HomeFragment.saturdayArray;
        arrays[6] = HomeFragment.sundayArray;
        for (int i = 0; i < arrays.length; i++) {
            if(arrays[i] != null) {
                for (int j = 0; j < arrays[i].length; j++) {
                    if (arrays[i][j] == 1)
                        checkBoxes[i][j].setChecked(true);
                }
            }
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                setupCheckBoxListener(i, j);
            }
        }

        Button confirmPlan = rootView.findViewById(R.id.confirmPlan);

        confirmPlan.setOnClickListener(v ->
        {
            saveArray.clear();
            saveArray.add(HomeFragment.mondayArray);
            saveArray.add(HomeFragment.tuesdayArray);
            saveArray.add(HomeFragment.wednesdayArray);
            saveArray.add(HomeFragment.thursdayArray);
            saveArray.add(HomeFragment.fridayArray);
            saveArray.add(HomeFragment.saturdayArray);
            saveArray.add(HomeFragment.sundayArray);

            JSONArray jsonArray = new JSONArray();
            for (int[] dayArray : new int[][]{HomeFragment.mondayArray, HomeFragment.tuesdayArray, HomeFragment.wednesdayArray, HomeFragment.thursdayArray, HomeFragment.fridayArray, HomeFragment.saturdayArray, HomeFragment.sundayArray}) {
                JSONArray dayJsonArray = new JSONArray();
                for (int item : dayArray) {
                    dayJsonArray.put(item);
                }
                jsonArray.put(dayJsonArray);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){FileUtility.savePlan(requireContext(), "personalplan.json", jsonArray.toString());}
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            int currentId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
            int targetId = R.id.navigation_home;
            if (navController.getCurrentDestination() != null && currentId == targetId)
                navController.popBackStack();
            navController.navigate(targetId);
        });

        return rootView;
    }

    private void setupCheckBoxListener(int dayIndex, int muscleIndex) {
        checkBoxes[dayIndex][muscleIndex].setOnCheckedChangeListener((buttonView, isChecked) -> {
            int[] targetArray = null;
            switch (dayIndex) {
                case 0:
                    targetArray = HomeFragment.mondayArray;
                    break;
                case 1:
                    targetArray = HomeFragment.tuesdayArray;
                    break;
                case 2:
                    targetArray = HomeFragment.wednesdayArray;
                    break;
                case 3:
                    targetArray = HomeFragment.thursdayArray;
                    break;
                case 4:
                    targetArray = HomeFragment.fridayArray;
                    break;
                case 5:
                    targetArray = HomeFragment.saturdayArray;
                    break;
                case 6:
                    targetArray = HomeFragment.sundayArray;
                    break;
            }
            if (targetArray != null) {
                targetArray[muscleIndex] = isChecked ? 1 : 0;
            }
        });
    }
}
