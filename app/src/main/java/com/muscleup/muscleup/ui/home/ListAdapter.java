package com.muscleup.muscleup.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;
import com.muscleup.muscleup.ui.workouts.WorkoutsFragment;

import java.util.ArrayList;
import java.util.Objects;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.WorkoutViewHolder>
{
    private ArrayList<ListModel> workouts;
    private FragmentManager fragmentManager;

    public ListAdapter(ArrayList<ListModel> workouts, FragmentManager fragmentManager)
    {
        this.workouts = workouts;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        ListModel workout = workouts.get(position);
        holder.bind(workout);

        holder.add_to_list.setOnClickListener(v ->
        {
            int w;
            int w2 = w2Fill(workout.getDifficulty());
            if(HomeFragment.dumbbellList.contains(workout.getName()))
                w = 5;
            else
                w = 0;

            SpannableString spannableString = new SpannableString(v.getContext().getString(R.string.saving_to_list));
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                    .setTitle(v.getContext().getString(R.string.question))
                    .setMessage(spannableString)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.custom, (dialog, whichButton) -> {
                        int reps = 0;
                        int sets = 0;
                        String result = Functions.isDefaultExercise(workout.getName());
                        if (!result.equals("0"))
                        {
                            String[] parts = result.split(" ");
                            reps = Integer.parseInt(parts[0]);
                            sets = Integer.parseInt(parts[1]);
                        }

                        boolean workoutExists = false;
                        for (WorkoutModel wm : HomeFragment.customArray) {
                            if(Objects.equals(wm.getName(), workout.getName()))
                            {
                                reps = wm.getReps();
                                sets = wm.getSets();
                            }
                            if (wm.getName().equals(workout.getName()))
                            {
                                wm.setSets(wm.getSets() + 1);
                                workoutExists = true;
                                break;
                            }
                        }
                        if (!workoutExists)
                            HomeFragment.customArray.add(new WorkoutModel(workout.getName(), reps, sets, w, w2));
                        HomeFragment.exercisesMap = WorkoutsFragment.convertToJsonStructure();
                        FileUtility.saveWorkouts(v.getContext(), HomeFragment.exercisesMap, "exercises.json");
                    })
                    .setNegativeButton(R.string.designated, (dialog, whichButton) -> {
                        ArrayList<WorkoutModel> properArray = null;
                        if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.absS)))
                            properArray = HomeFragment.absArray;
                        else if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.chestS)))
                            properArray = HomeFragment.chestArray;
                        else if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.backS)))
                            properArray = HomeFragment.backArray;
                        else if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.shouldersS)))
                            properArray = HomeFragment.shouldersArray;
                        else if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.armsS)))
                            properArray = HomeFragment.armsArray;
                        else if(Objects.equals(workout.getMuscleGroup(), v.getContext().getString(R.string.legsS)))
                            properArray = HomeFragment.legsArray;

                        if (properArray != null)
                        {
                            boolean workoutExists = false;
                            for (WorkoutModel wm : properArray) {
                                if (wm.getName().equals(workout.getName())) {
                                    wm.setSets(wm.getSets() + 1);
                                    workoutExists = true;
                                    break;
                                }
                            }
                            if (!workoutExists)
                                properArray.add(new WorkoutModel(workout.getName(), 10, 4, w, w2));
                        }
                        HomeFragment.exercisesMap = WorkoutsFragment.convertToJsonStructure();
                        FileUtility.saveWorkouts(v.getContext(), HomeFragment.exercisesMap, "exercises.json");
                    }).show();
        });

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
            fragmentManager.beginTransaction().replace(R.id.homepage, new HowToFragment(workout.getName()), "info").addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textViewName;
        private final TextView difficultyViewName;
        private final TextView muscleGroupViewName;
        private final Button add_to_list;
        private final ImageView image;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            difficultyViewName = itemView.findViewById(R.id.difficulty);
            muscleGroupViewName = itemView.findViewById(R.id.muscle_group);
            add_to_list = itemView.findViewById(R.id.add_to_list);
            image = itemView.findViewById(R.id.exerciseImage);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ListModel workout)
        {
            textViewName.setText(workout.getName());
            difficultyViewName.setText(itemView.getContext().getString(R.string.difficulty) + " " + workout.getDifficulty());
            muscleGroupViewName.setText(itemView.getContext().getString(R.string.muscle_groupp) + " " + workout.getMuscleGroup());
        }
    }

    int w2Fill(String diff)
    {
        int w2 = 0;
        if(diff.equals("easy") || diff.equals("łatwy"))
            w2 = 1;
        else if(diff.equals("medium") || diff.equals("średni"))
            w2 = 2;
        else if(diff.equals("hard") || diff.equals("trudny"))
            w2 = 3;
        return w2;
    }
}
