package com.muscleup.muscleup.ui.statistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.HomeFragment;

public class OtherStats extends Fragment
{
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.other_stats_layout, container, false);
        StatisticsFragment.personal_records.setClickable(false);
        StatisticsFragment.goals.setClickable(false);
        StatisticsFragment.today_stats.setClickable(false);

        long t = 0;
        if(HomeFragment.reps_failsafe.get(0) > 0)
            t = 2147483647L * HomeFragment.reps_failsafe.get(0);
        t += HomeFragment.homeWidgetValues.get(3);
        TextView today_form = root.findViewById(R.id.today_form);
        SpannableString spannableString = new SpannableString(getString(R.string.reps_done));
        RelativeSizeSpan smallerTextSize = new RelativeSizeSpan(0.9f);
        spannableString.setSpan(smallerTextSize, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan = new SpannableString(String.valueOf(t));
        numberSpan.setSpan(new RelativeSizeSpan(1.6f), 0, numberSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText = TextUtils.concat(spannableString, "\n", numberSpan);
        today_form.setText(finalText);

        TextView weight_lifted = root.findViewById(R.id.weight_lifted);
        SpannableString spannableString2 = new SpannableString(getString(R.string.weight_lifted));
        RelativeSizeSpan smallerTextSize2 = new RelativeSizeSpan(0.9f);
        spannableString2.setSpan(smallerTextSize2, 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan2 = new SpannableString(String.valueOf(HomeFragment.homeWidgetValues.get(4)));
        numberSpan2.setSpan(new RelativeSizeSpan(1.6f), 0, numberSpan2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText2 = TextUtils.concat(spannableString2, "\n", numberSpan2);
        weight_lifted.setText(finalText2);

        return root;
    }
}