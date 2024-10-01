package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.home.HowToFragment;
import com.muscleup.muscleup.ui.home.SessionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.WorkoutViewHolder>
{
    private final List<SessionModel> stats;
    private final FragmentManager fragmentManager;

    public StatsAdapter(ArrayList<SessionModel> stats, FragmentManager fragmentManager)
    {
        this.stats = stats;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_stats_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        SessionModel today = stats.get(position);
        holder.bind(today);

        holder.image.setOnClickListener(v -> {
            NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .setPopEnterAnim(android.R.anim.fade_in)
                        .setPopExitAnim(android.R.anim.fade_out)
                        .build();
                navController.navigate(R.id.navigation_home, null, navOptions);
            }
            fragmentManager.beginTransaction().replace(R.id.homepage, new HowToFragment(today.getName()), "info").addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textViewName;
        private final TextView numberViewName;
        private final ImageView image;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            numberViewName = itemView.findViewById(R.id.count);
            image = itemView.findViewById(R.id.exerciseImage);
        }

        @SuppressLint("SetTextI18n")
        public void bind(SessionModel statistic)
        {
            textViewName.setText(statistic.getName());
            String t = itemView.getContext().getString(R.string.reps_done2);
            if (Locale.getDefault().getLanguage().equals("pl"))
                if(HomeFragment.arrayOfSeconds.contains(Functions.stripe(statistic.getName())))
                    t = itemView.getContext().getString(R.string.reps_done2_replacement);
            numberViewName.setText(t + " " + statistic.getReps().toString());
        }
    }
}
