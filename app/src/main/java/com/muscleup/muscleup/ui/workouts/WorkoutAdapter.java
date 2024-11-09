package com.muscleup.muscleup.ui.workouts;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.home.HowToFragment;

import java.util.Collections;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {
    private final List<WorkoutModel> workouts;
    private final FragmentManager fragmentManager;
    private final String group;
    private final Context context;
    public static boolean ifBelow = false;

    public WorkoutAdapter(List<WorkoutModel> workouts, FragmentManager fragmentManager, String group, Context context) {
        this.workouts = workouts;
        this.fragmentManager = fragmentManager;
        this.group = group;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_exercise_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutModel workout = workouts.get(position);
        holder.bind(workout);
        ifBelow = false;
        HomeFragment.imageFailsafe = false;

        holder.delete.setOnClickListener(v -> {
            if (!ifBelow) {
                SpannableString spannableString = new SpannableString(v.getContext().getString(R.string.are_you_sure_you_want_to_delete));
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                        .setTitle(v.getContext().getString(R.string.warning))
                        .setMessage(spannableString)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            workouts.remove(position);
                            WorkoutAdapter.this.notifyDataSetChanged();
                            HomeFragment.exercisesMap = WorkoutsFragment.convertToJsonStructure();
                            FileUtility.saveWorkouts(v.getContext(), HomeFragment.exercisesMap, "exercises.json");
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        holder.edit.setOnClickListener(v -> {
            if (!ifBelow) {
                fragmentManager.beginTransaction().replace(R.id.exercisesLayout, new AddExerciseFragment(group, workout.getName(), workout.getReps(), workout.getSets(), workout.getWeight(), Functions.parseDifficulty(workout.getDifficulty())), "editExercise").addToBackStack(null).commit();
                WorkoutFragment.addExercises.setVisibility(View.GONE);
                WorkoutFragment.addExercises.setClickable(false);
                HomeFragment.imageFailsafe = true;
            }
        });

        holder.image.setOnClickListener(v -> {
            if(!HomeFragment.imageFailsafe && !Functions.isDefaultExercise(workout.getName()).equals("0"))
            {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(workouts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(workouts, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        saveWorkouts();
        return true;
    }

    private void saveWorkouts() {
        HomeFragment.exercisesMap = WorkoutsFragment.convertToJsonStructure();
        FileUtility.saveWorkouts(context, HomeFragment.exercisesMap, "exercises.json");
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView repsViewName;
        private final TextView setViewName;
        protected ImageView delete;
        protected ImageView edit;
        protected ImageView image;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            repsViewName = itemView.findViewById(R.id.number);
            setViewName = itemView.findViewById(R.id.sets);
            this.delete = itemView.findViewById(R.id.delete);
            this.edit = itemView.findViewById(R.id.edit);
            this.image = itemView.findViewById(R.id.exerciseImage);
        }

        @SuppressLint("SetTextI18n")
        public void bind(WorkoutModel workout) {
            textViewName.setText(workout.getName());
            String t = itemView.getContext().getString(R.string.reps2);
            if(HomeFragment.arrayOfSeconds.contains(workout.getName()))
                t = itemView.getContext().getString(R.string.reps2_replacement);
            String reps = t + workout.getReps().toString();
            if (workout.getWeight() > 1)
                repsViewName.setText(reps + " x " + workout.getWeight() + " kg");
            else
                repsViewName.setText(reps);
            setViewName.setText(itemView.getContext().getString(R.string.sets2) + workout.getSets().toString());
            if(Functions.isDefaultExercise(workout.getName()).equals("0"))
                this.image.setImageResource(R.drawable.image_gray);
            else
                this.image.setImageResource(R.drawable.image);
        }
    }
}
