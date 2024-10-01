package com.muscleup.muscleup.ui.awards;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.databinding.FragmentAwardsBinding;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.util.Locale;

public class AwardsFragment extends Fragment
{
    private FragmentAwardsBinding binding;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout push_up_challenge;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout pull_up_challenge;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout core_challenge;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAwardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeFragment.restFailsafe = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {HomeFragment.awardsArray = FileUtility.readAwards(requireContext(), "awards.json");}

        // 500 push-ups, 1000 push-ups, 500 pull-ups, 1000 pull-ups, 500 dips, 1000 dips, 100 workouts, 365 workouts, 100 hours spent exercising, 500 hours spent exercising, 50 challenges completed, 100 challenges completed
        for(int i = 1; i <= 12; i++)
        {
            String xId = "award" + i;
            String xId2 = "award" + i + "_2";
            String xId_pl = "award" + i + "_pl";
            String xId2_pl = "award" + i + "_2_pl";
            int resId = getResources().getIdentifier(xId, "id", requireActivity().getPackageName());
            ImageView award = root.findViewById(resId);
            int drawableId1 = 0;
            int drawableId2 = 0;

            if(Locale.getDefault().getLanguage().equals("pl"))
            {
                drawableId1 = getResources().getIdentifier(xId_pl, "drawable", requireActivity().getPackageName());
                drawableId2 = getResources().getIdentifier(xId2_pl, "drawable", requireActivity().getPackageName());
            }
            else
            {
                drawableId1 = getResources().getIdentifier(xId, "drawable", requireActivity().getPackageName());
                drawableId2 = getResources().getIdentifier(xId2, "drawable", requireActivity().getPackageName());
            }

            if (HomeFragment.awardsArray.size() > i - 1 && HomeFragment.awardsArray.get(i - 1) == 1)
                award.setImageResource(drawableId2);
            else
                award.setImageResource(drawableId1);
        }

        push_up_challenge = root.findViewById(R.id.push_up_challenge);
        pull_up_challenge = root.findViewById(R.id.pull_up_challenge);
        core_challenge = root.findViewById(R.id.core_challenge);
        setUpButtonListener(push_up_challenge, new ChallengeSession(1));
        setUpButtonListener(pull_up_challenge, new ChallengeSession(2));
        setUpButtonListener(core_challenge, new ChallengeSession(3));

        return root;
    }

    private void setUpButtonListener(LinearLayout button, ChallengeSession challengeSession)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}