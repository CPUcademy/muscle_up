package com.muscleup.muscleup.ui.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class ExerciseSession extends Fragment
{
    public WorkoutAdapterSession adapter;
    public static ArrayList<WorkoutModel> arrayEx = new ArrayList<>();
    private static ArrayList<WorkoutModel> arrayEx2 = new ArrayList<>();
    private static ArrayList<WorkoutModel> arrayExCopy = new ArrayList<>();
    private TextView exercise_now;
    public static Integer skip = 0;
    private static int[] array = null;
    public static long startTime;
    public static int repsCounter;
    public static boolean ifRest = false;
    public ArrayList<SessionModel> stats = new ArrayList<>();
    public ArrayList<SessionModel> statsFilled = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Button timer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.session_layout, container, false);
        HomeFragment.restFailsafe = false;
        startTime = 0;
        repsCounter = 0;
        HomeFragment.personal_plan.setClickable(false);
        HomeFragment.start_exercising.setClickable(false);
        HomeFragment.listOfExercises.setClickable(false);
        exercise_now = root.findViewById(R.id.exercise_now);
        timer = root.findViewById(R.id.timer);
        timer.setOnClickListener(v ->
        {
            WorkoutModel value = arrayEx.get(0);
            if(HomeFragment.arrayOfSeconds.contains(Functions.stripe(String.valueOf(value.getName()))))
                getParentFragmentManager().beginTransaction().replace(R.id.exLayout, new RestTimeFragment(HomeFragment.time)).addToBackStack(null).commit();
        });

        fillArrayEx1();
        if(!Functions.containsOnlyZeros(array))
        {
            fillArrayEx2();
            RecyclerView recyclerView = root.findViewById(R.id.trainingExercisesList);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            startTime = System.currentTimeMillis();
            adapter = new WorkoutAdapterSession(arrayEx);
            recyclerView.setAdapter(adapter);
            arrayExCopy = new ArrayList<>(arrayEx);
            setText();
            for(int f = 0; f < arrayEx.size(); f++)
               stats.add(new SessionModel(Functions.stripe(arrayEx.get(f).getName()), 0));
        }
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void fillArrayEx1()
    {
        arrayEx = new ArrayList<>();
        arrayEx2 = new ArrayList<>();
        arrayExCopy = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        dayOfWeekNumber -= 1;

        array = null;
        switch(dayOfWeekNumber)
        {
            case 0:
                array = HomeFragment.mondayArray;
                break;
            case 1:
                array = HomeFragment.tuesdayArray;
                break;
            case 2:
                array = HomeFragment.wednesdayArray;
                break;
            case 3:
                array = HomeFragment.thursdayArray;
                break;
            case 4:
                array = HomeFragment.fridayArray;
                break;
            case 5:
                array = HomeFragment.saturdayArray;
                break;
            case 6:
                array = HomeFragment.sundayArray;
                break;
        }
    }

    public static void fillArrayEx2()
    {
        ArrayList<String> groups = new ArrayList<>();

        for (int i = 0; i < 7; i++)
        {
            if (array[i] == 1)
            {
                switch (i) {
                    case 0:
                        groups.add("abs");
                        break;
                    case 1:
                        groups.add("chest");
                        break;
                    case 2:
                        groups.add("back");
                        break;
                    case 3:
                        groups.add("shoulders");
                        break;
                    case 4:
                        groups.add("arms");
                        break;
                    case 5:
                        groups.add("legs");
                        break;
                    case 6:
                        groups.add("custom");
                        break;
                }
            }
        }

        for(int i = 0; i < groups.size(); i++)
        {
            arrayEx2 = new ArrayList<>();
            if(Objects.equals(groups.get(i), "abs"))
                arrayEx2 = new ArrayList<>(HomeFragment.absArray);
            else if(Objects.equals(groups.get(i), "chest"))
                arrayEx2 = new ArrayList<>(HomeFragment.chestArray);
            else if(Objects.equals(groups.get(i), "back"))
                arrayEx2 = new ArrayList<>(HomeFragment.backArray);
            else if(Objects.equals(groups.get(i), "shoulders"))
                arrayEx2 = new ArrayList<>(HomeFragment.shouldersArray);
            else if(Objects.equals(groups.get(i), "arms"))
                arrayEx2 = new ArrayList<>(HomeFragment.armsArray);
            else if(Objects.equals(groups.get(i), "legs"))
                arrayEx2 = new ArrayList<>(HomeFragment.legsArray);
            else if(Objects.equals(groups.get(i), "custom"))
                arrayEx2 = new ArrayList<>(HomeFragment.customArray);

            Functions.sessionArrayFill(arrayEx, arrayEx2);
        }
    }

    public void setText(){Functions.setText(arrayEx, arrayExCopy, statsFilled, exercise_now, startTime, skip, repsCounter, requireContext());}
}
