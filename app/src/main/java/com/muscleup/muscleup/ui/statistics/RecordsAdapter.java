package com.muscleup.muscleup.ui.statistics;

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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;

import java.util.List;
import java.util.Locale;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.WorkoutViewHolder>
{
    private final List<RecordModel> records;
    public static boolean ifBelow = false;
    private final FragmentManager fragmentManager;

    public RecordsAdapter(List<RecordModel> records, FragmentManager fragmentManager)
    {
        this.records = records;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_record_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        RecordModel workout = records.get(position);
        holder.bind(workout);

        ifBelow = false;

        holder.delete.setOnClickListener(v ->
        {
            if(!ifBelow)
            {
                SpannableString spannableString = new SpannableString(v.getContext().getString(R.string.are_you_sure_you_want_to_delete));
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                        .setTitle(R.string.warning)
                        .setMessage(spannableString)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                        {
                            records.remove(position);
                            RecordsAdapter.this.notifyDataSetChanged();
                            FileUtility.saveRecords(v.getContext(), "personalrecords.json", StatisticsFragment.recordsArray);
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        holder.edit.setOnClickListener(v ->
        {
            if(!ifBelow)
            {
                fragmentManager.beginTransaction().replace(R.id.recordsLayout, new AddRecordFragment(workout.getName(), workout.getNumber())).addToBackStack(null).commit();
                RecordFragment.addExercises.setVisibility(View.GONE);
                RecordFragment.addExercises.setClickable(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textViewName;
        private final TextView indexViewName;
        protected ImageView delete;
        protected ImageView edit;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            indexViewName = itemView.findViewById(R.id.number);
            this.delete = itemView.findViewById(R.id.delete);
            this.edit = itemView.findViewById(R.id.edit);
        }

        @SuppressLint("SetTextI18n")
        public void bind(RecordModel workout)
        {
            textViewName.setText(workout.getName());
            if(Locale.getDefault().getLanguage().equals("pl"))
                if(workout.getNumber().toLowerCase().contains("reps")) {
                    String originalText = workout.getNumber();
                    int index = originalText.indexOf(" ");
                    String number = originalText.substring(0, index);
                    indexViewName.setText(number + " " + itemView.getContext().getString(R.string.reps_powt));
                }
            else{indexViewName.setText(workout.getNumber());}
        }
    }
}
