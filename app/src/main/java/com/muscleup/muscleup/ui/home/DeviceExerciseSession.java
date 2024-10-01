package com.muscleup.muscleup.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.settings.SettingsFragment;

public class DeviceExerciseSession extends Fragment
{
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.device_session_layout, container, false);
        HomeFragment.personal_plan.setClickable(false);

        ExerciseSession.fillArrayEx1();
        ExerciseSession.fillArrayEx2();
        String sessionString = Functions.buildSessionString(ExerciseSession.arrayEx);
        Functions.send("S|" + SettingsFragment.restTime + "|" + sessionString);

        return rootView;
    }
}
