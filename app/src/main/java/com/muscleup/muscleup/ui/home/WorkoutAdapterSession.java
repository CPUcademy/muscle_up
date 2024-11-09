package com.muscleup.muscleup.ui.home;

import static com.muscleup.muscleup.ui.home.HomeFragment.exS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.settings.SettingsFragment;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import java.util.List;

public class WorkoutAdapterSession extends RecyclerView.Adapter<WorkoutAdapterSession.WorkoutViewHolder>
{
    private final List<WorkoutModel> workouts;

    public WorkoutAdapterSession(List<WorkoutModel> workouts)
    {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_workout_exercise_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutModel workout = workouts.get(position);
        holder.bind(workout);

        holder.skip.setOnClickListener(v ->
        {
            if(!ExerciseSession.ifRest)
            {
                SpannableString spannableString = new SpannableString(v.getContext().getString(R.string.do_you_want_to_skip_this_exercise));
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                        .setTitle(v.getContext().getString(R.string.warning))
                        .setMessage(spannableString)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                        {
                            workouts.remove(position);
                            ExerciseSession.skip++;
                            exS.setText();
                            WorkoutAdapterSession.this.notifyDataSetChanged();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        holder.done.setOnClickListener(v ->
        {
            if(!ExerciseSession.ifRest)
            {
                boolean found = false;
                for (SessionModel session : exS.statsFilled) {
                    if (session.getName().equals(Functions.stripe(exS.arrayEx.get(0).getName()))) {
                        int t = SessionModel.getRepsByName(exS.statsFilled, Functions.stripe(ExerciseSession.arrayEx.get(0).getName()));
                        int t2 = 0;
                        t += ExerciseSession.arrayEx.get(0).getReps();
                        t2 += ExerciseSession.arrayEx.get(0).getReps();
                        ExerciseSession.repsCounter += t2;
                        session.setReps(t);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    int t = ExerciseSession.arrayEx.get(0).getReps();
                    exS.statsFilled.add(new SessionModel(Functions.stripe(ExerciseSession.arrayEx.get(0).getName()), t));
                    ExerciseSession.repsCounter += t;
                }

                ExerciseSession.arrayEx.remove(0);
                exS.adapter.notifyDataSetChanged();
                exS.setText();
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                if(SettingsFragment.restTime != 0)
                    fragmentManager.beginTransaction().replace(R.id.exLayout, new RestTimeFragment()).addToBackStack(null).commit();

                if(workout.getWeight() > 1) {
                    HomeFragment.homeWidgetValues.set(4, HomeFragment.homeWidgetValues.get(4) + workout.getWeight());
                    FileUtility.saveStats(v.getContext(), "stats.json", HomeFragment.homeWidgetValues);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textViewName;
        private final TextView indexViewName;
        private final TextView weightViewName;
        protected ImageView skip;
        protected ImageView done;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            indexViewName = itemView.findViewById(R.id.number);
            weightViewName = itemView.findViewById(R.id.weight);
            skip = itemView.findViewById(R.id.skip);
            done = itemView.findViewById(R.id.done);
        }

        @SuppressLint("SetTextI18n")
        public void bind(WorkoutModel workout)
        {
            textViewName.setText(workout.getName());
            indexViewName.setText(workout.getReps().toString());
            if(workout.getWeight() > 1)
                weightViewName.setText("x " + workout.getWeight().toString() + " kg");
        }
    }
}
