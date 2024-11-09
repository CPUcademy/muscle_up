package com.muscleup.muscleup.ui.awards;

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
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.home.RestTimeFragment;
import com.muscleup.muscleup.ui.home.SessionModel;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import java.util.ArrayList;

public class ChallengeSession extends Fragment
{
    public ChallengeWorkoutAdapterSession adapter;
    public static ArrayList<WorkoutModel> arrayEx = new ArrayList<>();
    private static ArrayList<WorkoutModel> arrayExCopy = new ArrayList<>();
    private TextView exercise_now;
    public static int skip = 0;
    public static long startTime;
    public static int repsCounter;
    private final int whichChallenge;
    public ArrayList<SessionModel> stats = new ArrayList<>();
    public ArrayList<SessionModel> statsFilled = new ArrayList<>();

    public ChallengeSession(int whichChallenge){this.whichChallenge = whichChallenge;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.session_layout, container, false);
        HomeFragment.restFailsafe = false;
        startTime = 0;
        repsCounter = 0;
        arrayEx = new ArrayList<>();
        ArrayList<WorkoutModel> arrayEx2 = new ArrayList<>();
        arrayExCopy = new ArrayList<>();
        exercise_now = root.findViewById(R.id.exercise_now);
        Button timer = root.findViewById(R.id.timer);
        timer.setOnClickListener(v ->
        {
            WorkoutModel value = arrayEx.get(0);
            if(HomeFragment.arrayOfSeconds.contains(Functions.stripe(String.valueOf(value.getName()))))
                getParentFragmentManager().beginTransaction().replace(R.id.exLayout, new RestTimeFragment(HomeFragment.time)).addToBackStack(null).commit();
        });

        if(whichChallenge == 1)
            arrayEx2 = HomeFragment.push_up_challenge;
        else if(whichChallenge == 2)
            arrayEx2 = HomeFragment.pull_up_challenge;
        else if(whichChallenge == 3)
            arrayEx2 = HomeFragment.core_challenge;
        else if(whichChallenge == 4)
            arrayEx2 = HomeFragment.muscle_up_skill;
        else if(whichChallenge == 5)
            arrayEx2 = HomeFragment.l_sit_skill;
        else if(whichChallenge == 6)
            arrayEx2 = HomeFragment.handstand_skill;

        if(whichChallenge == 1 || whichChallenge == 2 || whichChallenge == 3)
            HomeFragment.ifChallenge = true;

        Functions.sessionArrayFill(arrayEx, arrayEx2);
        RecyclerView recyclerView = root.findViewById(R.id.trainingExercisesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        startTime = System.currentTimeMillis();
        adapter = new ChallengeWorkoutAdapterSession(arrayEx);
        recyclerView.setAdapter(adapter);
        arrayExCopy = new ArrayList<>(arrayEx);
        setText();

        for(int f = 0; f < arrayEx.size(); f++)
           stats.add(new SessionModel(Functions.stripe(arrayEx.get(f).getName()), 0));

        return root;
    }

    public void setText(){Functions.setText(arrayEx, arrayExCopy, statsFilled, exercise_now, startTime, skip, repsCounter, requireContext());}
}
