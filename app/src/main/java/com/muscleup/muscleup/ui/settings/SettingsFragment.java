package com.muscleup.muscleup.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.databinding.FragmentSettingsBinding;
import com.muscleup.muscleup.ui.home.HomeFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SettingsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private FragmentSettingsBinding binding;
    @SuppressLint("StaticFieldLeak")
    public static EditText dateEditText;
    @SuppressLint({"StaticFieldLeak", "UseSwitchCompatOrMaterialCode"})
    public static Switch pullUpEquipment;
    @SuppressLint({"StaticFieldLeak", "UseSwitchCompatOrMaterialCode"})
    public static Switch dipsEquipment;
    @SuppressLint({"StaticFieldLeak", "UseSwitchCompatOrMaterialCode"})
    public static Switch dumbbellsEquipment;
    @SuppressLint("StaticFieldLeak")
    public static Spinner restTimeS;
    public static boolean defaultSettings = false;
    public static String dataPullUps;
    public static String dataDips;
    public static String dataDumbbells;
    public static boolean isCheckedPullUps;
    public static boolean isCheckedDips;
    public static boolean isCheckedDumbbells;
    public static Date dataVacation;
    public static Integer restTime = 5;
    public static ArrayList<String> settingsArray;
    public static ArrayList<Integer> arrayOfTrainingDays = new ArrayList<>();
    private final List<String> jsonFiles = Arrays.asList("awards.json", "awardsStats.json", "exercises.json", "personalrecords.json", "personalplan.json",
            "settings.json", "stats.json", "todaystats.json", "form.json", "graphFormData.json", "last_date.json", "trainingDays.json", "supported.json",
            "dumbbellExercises.json", "pullupExercises.json", "dipExercises.json", "weight.json", "secondsExercises.json", "exercisesList.json", "descriptions.json",
            "descriptionsPl.json", "links.json", "reps_failsafe.json", "challenges.json", "weightProgress.json");
    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    DateFormat dateFormat2 = DateFormat.getDateInstance(DateFormat.DEFAULT);

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeFragment.restFailsafe = true;

        dateEditText = root.findViewById(R.id.dateOfVacation);
        pullUpEquipment = root.findViewById(R.id.pull_up_bar);
        setupEquipment(pullUpEquipment, dataPullUps, 1);
        dipsEquipment = root.findViewById(R.id.dip_rails);
        setupEquipment(dipsEquipment, dataDips, 2);
        dumbbellsEquipment = root.findViewById(R.id.dumbbells);
        setupEquipment(dumbbellsEquipment, dataDumbbells, 3);
        ArrayList<String> numbersForSpinner = new ArrayList<>();
        for (int i = 0; i <= 180; i++)
            numbersForSpinner.add(String.valueOf(i));

        restTimeS = root.findViewById(R.id.rest_time);
        ArrayAdapter<String> adapterTime = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, numbersForSpinner);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restTimeS.setAdapter(adapterTime);
        int position = numbersForSpinner.indexOf(String.valueOf(restTime));
        if (position >= 0)
            restTimeS.setSelection(position);

        restTimeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                restTime = Integer.valueOf(selectedItem);
                settingsArray.set(4, selectedItem);
                FileUtility.saveSettings(requireContext(), "settings.json", settingsArray);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if(SettingsFragment.dataVacation.compareTo(new Date()) <= 0)
            SettingsFragment.dateEditText.setText(SettingsFragment.getCurrentDate());
        else
        {
            String formattedDate = dateFormat2.format(SettingsFragment.dataVacation);
            SettingsFragment.dateEditText.setText(formattedDate);
        }

        requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();

        dateEditText.setOnClickListener(v ->
        {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), SettingsFragment.this, year, month, day);
            datePickerDialog.show();
        });

        Button defaults = root.findViewById(R.id.defaults);
        defaults.setOnClickListener(v ->
        {
            SpannableString spannableString = new SpannableString(getString(R.string.do_you_want_to_go_to_default_settings));
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                    .setTitle(getString(R.string.warning))
                    .setMessage(spannableString)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                    {
                        defaultSettings = true;
                        for(int i = 0; i < jsonFiles.size(); i++)
                            FileUtility.copyFileToInternalStorage(requireContext(), jsonFiles.get(i));
                        defaultSettings = false;
                        SettingsFragment.settingsArray.set(4, "30");
                        restTimeS.setSelection(Integer.parseInt(SettingsFragment.settingsArray.get(4)));

                        SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("HomeFragmentPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("LastExecutionDate", FileUtility.readSettings(requireContext(), "last_date.json").get(0));
                        editor.apply();
                        HomeFragment.lastFormValue2 = false;

                        Toast.makeText(requireContext(), getString(R.string.return_to_main_page), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        Button manualButton = root.findViewById(R.id.manual);
        manualButton.setOnClickListener(v ->
        {
            String url = "https://drive.google.com/drive/folders/11GjKp_WQpEEjrLoXUmRRxH2xsDvoIytp?usp=sharing";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        ChipGroup chipGroup = root.findViewById(R.id.trainingDays);
        ArrayList<String> days = new ArrayList<>(Arrays.asList(getString(R.string.m), getString(R.string.t), getString(R.string.w), getString(R.string.t2), getString(R.string.f), getString(R.string.s1), getString(R.string.s)));

        int i = 0;
        for(String s: days)
        {
            @SuppressLint("InflateParams") Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.chip_layout, null);
            chip.setText(s);
            chip.setId(View.generateViewId());
            if(arrayOfTrainingDays.get(i) == 1)
                chip.setChecked(true);
            chipGroup.addView(chip);
            i++;
        }

        chipGroup.setOnCheckedStateChangeListener((chipGroup1, checkedIds) ->
        {
            if(!checkedIds.isEmpty())
            {
                arrayOfTrainingDays = new ArrayList<>(Collections.nCopies(7, 0));
                Collections.fill(arrayOfTrainingDays, 0);
                for (int id : checkedIds) {
                    int chipIndex = chipGroup1.indexOfChild(chipGroup1.findViewById(id));
                    if (chipIndex != -1) {
                        arrayOfTrainingDays.set(chipIndex, 1);
                    }
                }
                FileUtility.saveStats(requireContext(), "trainingDays.json", arrayOfTrainingDays);
            }
        });

        return root;
    }

    private void setupEquipment(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchh, String data, int index)
    {
        Object[] objects = new Object[]{isCheckedPullUps, isCheckedDips, isCheckedDumbbells};
        int index2 = index - 1;
        switchh.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {
                settingsArray.set(index, "Y");
                objects[index2] = true;
            } else {
                settingsArray.set(index, "N");
                objects[index2] = false;
            }
            FileUtility.saveSettings(requireContext(), "settings.json", settingsArray);
        });
        switchh.setChecked(Objects.equals(data, "Y"));
        objects[index2] = Objects.equals(data, "Y");
    }


    private void send(String s){DevicesFragment.fragment.sending(s);}

    public static String getCurrentDate()
    {
        dateFormat = DateFormat.getDateInstance();
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);
        Calendar today = Calendar.getInstance();
        if (!selectedDate.before(today))
        {
            selectedDate.set(year, month, dayOfMonth);
            dateEditText.setText(dateFormat2.format(selectedDate.getTime()));
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            String formattedDate = formatDate.format(selectedDate.getTime());
            settingsArray.set(0, formattedDate);
            FileUtility.saveSettings(requireContext(), "settings.json", settingsArray);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
