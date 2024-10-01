package com.muscleup.muscleup.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.R;

import java.util.Locale;
import java.util.Objects;

public class HowToFragment extends Fragment
{
    private String name;

    public HowToFragment(String name)
    {
        this.name = name;
    }

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.how_to_layout, container, false);
        HomeFragment.personal_plan.setClickable(false);
        HomeFragment.start_exercising.setClickable(false);
        HomeFragment.listOfExercises.setClickable(false);

        WebView video = root.findViewById(R.id.video);
        video.getSettings().setJavaScriptEnabled(true);
        video.setWebChromeClient(new WebChromeClient());
        if(HomeFragment.links.containsKey(name))
            video.loadUrl(Objects.requireNonNull(HomeFragment.links.get(name)));

        Button title = root.findViewById(R.id.title);
        TextView description = root.findViewById(R.id.description_content);
        title.setText(name);
        if(Locale.getDefault().getLanguage().equals("pl"))
            description.setText(HomeFragment.descriptionsPl.get(name));
        else
            description.setText(HomeFragment.descriptions.get(name));

        return root;
    }
}