package com.muscleup.muscleup.ui.workouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.awards.ChallengeSession;
import com.muscleup.muscleup.ui.home.HomeFragment;

public class SkillsFragment extends Fragment
{
    @SuppressLint("StaticFieldLeak")
    public static ConstraintLayout muscle_up_skill;
    @SuppressLint("StaticFieldLeak")
    public static ConstraintLayout l_sit_skill;
    @SuppressLint("StaticFieldLeak")
    public static ConstraintLayout handstand_skill;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.skills_layout, container, false);

        muscle_up_skill = root.findViewById(R.id.muscle_up_skill);
        l_sit_skill = root.findViewById(R.id.l_sit_skill);
        handstand_skill = root.findViewById(R.id.handstand_skill);
        setUpButtonListener(muscle_up_skill, new ChallengeSession(4));
        setUpButtonListener(l_sit_skill, new ChallengeSession(5));
        setUpButtonListener(handstand_skill, new ChallengeSession(6));

        return root;
    }

    private void setUpButtonListener(ConstraintLayout button, ChallengeSession challengeSession)
    {
        button.setOnClickListener(v -> {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .setPopEnterAnim(android.R.anim.fade_in)
                        .setPopExitAnim(android.R.anim.fade_out)
                        .build();
                navController.navigate(R.id.navigation_home, null, navOptions);
            }
            HomeFragment.chS = challengeSession;
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homepage, HomeFragment.chS, "session").commit();
        });
    }
}