package com.muscleup.muscleup.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.settings.SettingsFragment;

public class RestTimeFragment extends Fragment
{
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView progressText;
    private final Handler handler = new Handler();
    private int time;
    private boolean ifTitle = false;

    public RestTimeFragment(int time)
    {
        this.time = time;
        ifTitle = true;
    }
    public RestTimeFragment(){time = SettingsFragment.restTime;}

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rest_time_layout, container, false);

        ExerciseSession.ifRest = true;
        TextView title = root.findViewById(R.id.rest_time_title);
        if(ifTitle)
            title.setText(R.string.timer);
        progressBar = root.findViewById(R.id.progressBar);
        progressText = root.findViewById(R.id.progressText);
        progressBar.setMax(time);
        new Thread(() -> {
            while (progressStatus < time) {
                progressStatus += 1;
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    progressText.setText(progressStatus + " / " + progressBar.getMax() + " s");
                });
                if(!HomeFragment.restFailsafe) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            getParentFragmentManager().popBackStack();
            ExerciseSession.ifRest = false;
        }).start();

        return root;
    }
}