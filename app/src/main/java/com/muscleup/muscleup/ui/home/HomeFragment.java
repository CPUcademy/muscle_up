package com.muscleup.muscleup.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.FileUtility;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.databinding.FragmentHomeBinding;
import com.muscleup.muscleup.ui.awards.ChallengeSession;
import com.muscleup.muscleup.ui.settings.SettingsFragment;
import com.muscleup.muscleup.ui.settings.TerminalFragment;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment
{
    private FragmentHomeBinding binding;
    private TextView streak;
    private TextView hours_spent_exercising;
    private TextView workouts_done;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout personal_plan;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout start_exercising;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout listOfExercises;
    public static int[] mondayArray;
    public static int[] tuesdayArray;
    public static int[] wednesdayArray;
    public static int[] thursdayArray;
    public static int[] fridayArray;
    public static int[] saturdayArray;
    public static int[] sundayArray;
    private boolean defaultSettingsValidator = false;
    public static boolean ifStreak = false;
    public static ArrayList<WorkoutModel> absArray = new ArrayList<>(); // ["name", reps, sets, weight, difficulty (1-3)]
    public static ArrayList<WorkoutModel> chestArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> backArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> shouldersArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> armsArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> legsArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> customArray = new ArrayList<>();
    public static ArrayList<WorkoutModel> absArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> chestArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> backArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> shouldersArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> armsArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> legsArrayList = new ArrayList<>();
    public static ArrayList<WorkoutModel> push_up_challenge = new ArrayList<>();
    public static ArrayList<WorkoutModel> pull_up_challenge = new ArrayList<>();
    public static ArrayList<WorkoutModel> core_challenge = new ArrayList<>();
    public static ArrayList<WorkoutModel> muscle_up_skill = new ArrayList<>();
    public static ArrayList<WorkoutModel> l_sit_skill = new ArrayList<>();
    public static ArrayList<WorkoutModel> handstand_skill = new ArrayList<>();
    public static HashMap<String, ArrayList<ArrayList<Object>>> exercisesMap;
    public static HashMap<String, ArrayList<ArrayList<Object>>> exercisesMap2;
    public static HashMap<String, ArrayList<ArrayList<Object>>> exercisesMap3;
    @SuppressLint("StaticFieldLeak")
    public static ExerciseSession exS = new ExerciseSession();
    @SuppressLint("StaticFieldLeak")
    public static ChallengeSession chS;
    public static boolean lastFormValue2 = true;
    public static ArrayList<Integer> homeWidgetValues;
    public static ArrayList<Integer> formWidgetValues;
    public static ArrayList<Integer> reps_failsafe;
    public static ArrayList<Integer> unprocessedAwardsValues;
    public static ArrayList<Integer> weight = new ArrayList<>();
    public static ArrayList<Integer> awardsArray = new ArrayList<>();
    public static ArrayList<String> supportedExercises = new ArrayList<>();
    public static ArrayList<String> dumbbellList = new ArrayList<>();
    public static ArrayList<String> pullupList = new ArrayList<>();
    public static ArrayList<String> dipList = new ArrayList<>();
    public static HashMap<String, String> descriptions = new HashMap<>();
    public static HashMap<String, String> descriptionsPl = new HashMap<>();
    public static HashMap<String, String> links = new HashMap<>();
    public static String[] muscleGroups = {"abs", "chest", "back", "shoulders", "arms", "legs", "custom"};
    public static ArrayList<String> arrayOfSeconds = new ArrayList<>();
    public static int time = 0;
    public static boolean restFailsafe = false;
    public static boolean imageFailsafe = false;
    public static boolean ifChallenge = false;
    private boolean ifStreakFreeze = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        restFailsafe = true;

        if(!defaultSettingsValidator)
        {
            SettingsFragment.defaultSettings = false;
            defaultSettingsValidator = true;
        }

        listOfExercises = root.findViewById(R.id.list_of_exercises);
        listOfExercises.setOnClickListener(v ->
        {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homepage, new ListOfExercisesFragment(), "list_of_exercises").commit();
        });

        absArray = new ArrayList<>();
        chestArray = new ArrayList<>();
        shouldersArray = new ArrayList<>();
        backArray = new ArrayList<>();
        armsArray = new ArrayList<>();
        legsArray = new ArrayList<>();
        absArrayList = new ArrayList<>();
        chestArrayList = new ArrayList<>();
        shouldersArrayList = new ArrayList<>();
        backArrayList = new ArrayList<>();
        armsArrayList = new ArrayList<>();
        legsArrayList = new ArrayList<>();
        customArray = new ArrayList<>();
        push_up_challenge = new ArrayList<>();
        pull_up_challenge = new ArrayList<>();
        core_challenge = new ArrayList<>();
        muscle_up_skill = new ArrayList<>();
        l_sit_skill = new ArrayList<>();
        handstand_skill = new ArrayList<>();

        FileUtility.copyFileToInternalStorage(requireContext(), "exercises.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "exercisesList.json");
        readFromExercisesArray();
        readFromExercisesArray2();

        FileUtility.copyFileToInternalStorage(requireContext(), "personalplan.json");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){readFromPersonalPlanArray();}
        FileUtility.copyFileToInternalStorage(requireContext(), "settings.json");
        readFromSettingsArray();

        FileUtility.copyFileToInternalStorage(requireContext(), "trainingDays.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "descriptions.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "descriptionsPl.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "links.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "supported.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "dumbbellExercises.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "pullupExercises.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "dipExercises.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "weight.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "secondsExercises.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "reps_failsafe.json");
        supportedExercises = FileUtility.readSettings(requireContext(), "supported.json");
        dumbbellList = FileUtility.readSettings(requireContext(), "dumbbellExercises.json");
        pullupList = FileUtility.readSettings(requireContext(), "pullupExercises.json");
        dipList = FileUtility.readSettings(requireContext(), "dipExercises.json");
        weight = FileUtility.readStats(requireContext(), "weight.json");
        arrayOfSeconds = FileUtility.readSettings(requireContext(), "secondsExercises.json");
        descriptions = FileUtility.readHashMap(requireContext(), "descriptions.json");
        descriptionsPl = FileUtility.readHashMap(requireContext(), "descriptionsPl.json");
        links = FileUtility.readHashMap(requireContext(), "links.json");
        reps_failsafe = FileUtility.readStats(requireContext(), "reps_failsafe.json");

        SettingsFragment.arrayOfTrainingDays = FileUtility.readStats(requireContext(), "trainingDays.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "challenges.json");
        readFromChallengesArray();

        FileUtility.copyFileToInternalStorage(requireContext(), "awardsStats.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "weightProgress.json");
        unprocessedAwardsValues = FileUtility.readAwards(requireContext(), "awardsStats.json");

        FileUtility.copyFileToInternalStorage(requireContext(), "awards.json");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {awardsArray = FileUtility.readAwards(requireContext(), "awards.json");}

        FileUtility.copyFileToInternalStorage(requireContext(), "last_date.json");
        FileUtility.copyFileToInternalStorage(requireContext(), "stats.json");
        streak = root.findViewById(R.id.streak);
        hours_spent_exercising = root.findViewById(R.id.hours_spent_exercising);
        workouts_done = root.findViewById(R.id.workouts_done);
        homeWidgetValues = FileUtility.readStats(requireContext(), "stats.json");
        readFromStatsArray();

        FileUtility.copyFileToInternalStorage(requireContext(), "form.json");
        formWidgetValues = FileUtility.readStats(requireContext(), "form.json");

        personal_plan = root.findViewById(R.id.design_your_training_plan);
        start_exercising = root.findViewById(R.id.start_your_training_session);

        personal_plan.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homepage, new PersonalPlanFragment(), "plan").commit());

        start_exercising.setOnClickListener(v ->
        {
            Fragment existingFragment = requireActivity().getSupportFragmentManager().findFragmentByTag("session");
            if (existingFragment != null)
                requireActivity().getSupportFragmentManager().beginTransaction().remove(existingFragment).commit();
            if (!TerminalFragment.ifConnected)
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homepage, exS, "session").addToBackStack(null).commit();
            else
            {
                SpannableString spannableString = new SpannableString(getString(R.string.starting_session));
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                        .setTitle(R.string.warning)
                        .setMessage(spannableString)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homepage, new DeviceExerciseSession(), "device-session").addToBackStack(null).commit();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        TextView today_form = root.findViewById(R.id.today_form);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        long lastFormTimestamp = prefs.getLong("last_form_timestamp", -1);
        int lastFormValue = prefs.getInt("last_form_value", -1);

        if (lastFormTimestamp != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lastFormTimestamp);

            Calendar today = Calendar.getInstance();
            boolean isSameDay = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

            if(isSameDay && lastFormValue != -1 && lastFormValue2) {
                SpannableString spannableString4 = new SpannableString(getString(R.string.your_form_today));
                RelativeSizeSpan smallerTextSize4 = new RelativeSizeSpan(0.85f);
                spannableString4.setSpan(smallerTextSize4, 0, spannableString4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString numberSpan4 = new SpannableString(lastFormValue + "%");
                numberSpan4.setSpan(new RelativeSizeSpan(1.65f), 0, numberSpan4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                CharSequence finalText4 = TextUtils.concat(spannableString4, "\n", numberSpan4);
                today_form.setText(finalText4);
            } else {
                today_form.setText(getString(R.string.your_form_today));
            }
        } else {
            today_form.setText(getString(R.string.your_form_today));
        }
        return root;
    }

    private void readFromChallengesArray()
    {
        exercisesMap2 = FileUtility.readWorkouts(requireContext(), "challenges.json");
        if (exercisesMap2 != null) {
            for (String key : exercisesMap2.keySet()) {
                List<ArrayList<Object>> exercises = exercisesMap2.get(key);
                ArrayList<WorkoutModel> workoutArray = new ArrayList<>();
                assert exercises != null;
                for (ArrayList<Object> exercise : exercises) {
                    String exerciseName = (String) exercise.get(0);
                    int repetitions = (int) exercise.get(1);
                    int sets = (int) exercise.get(2);
                    int weight = (int) exercise.get(3);
                    int difficulty = (int) exercise.get(4);
                    WorkoutModel t = new WorkoutModel(exerciseName, repetitions, sets, weight, difficulty);
                    workoutArray.add(t);
                }
                switch (key) {
                    case "push_up_challenge":
                        push_up_challenge.addAll(workoutArray);
                        break;
                    case "pull_up_challenge":
                        pull_up_challenge.addAll(workoutArray);
                        break;
                    case "core_challenge":
                        core_challenge.addAll(workoutArray);
                        break;
                    case "muscle_up":
                        muscle_up_skill.addAll(workoutArray);
                        break;
                    case "L-sit":
                        l_sit_skill.addAll(workoutArray);
                        break;
                    case "handstand":
                        handstand_skill.addAll(workoutArray);
                        break;
                }
            }
        }
    }

    private void readFromSettingsArray()
    {
        SettingsFragment.settingsArray = FileUtility.readSettings(requireContext(), "settings.json");
        Date parsedDate;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parsedDate = dateFormat.parse(SettingsFragment.settingsArray.get(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SettingsFragment.dataVacation = parsedDate;
        SettingsFragment.dataPullUps = SettingsFragment.settingsArray.get(1);
        SettingsFragment.dataDips = SettingsFragment.settingsArray.get(2);
        SettingsFragment.dataDumbbells = SettingsFragment.settingsArray.get(3);
        SettingsFragment.restTime = Integer.valueOf(SettingsFragment.settingsArray.get(4));

        SettingsFragment.isCheckedPullUps = SettingsFragment.dataPullUps.equals("Y");
        SettingsFragment.isCheckedDips = SettingsFragment.dataDips.equals("Y");
        SettingsFragment.isCheckedDumbbells = SettingsFragment.dataDumbbells.equals("Y");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void readFromStatsArray()
    {
        ArrayList<String> last_date = FileUtility.readSettings(requireContext(), "last_date.json");
        Date parsedDateL;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatt = new SimpleDateFormat("dd.MM.yyyy");
            parsedDateL = dateFormatt.parse(last_date.get(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        calendar.setTime(parsedDateL);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date parsedDateWithoutTime = calendar.getTime();

        if(!SettingsFragment.dataVacation.after(today))
        {
            ifStreak = false;
            if (parsedDateWithoutTime.before(today)) {
                LocalDate currentDat = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentDat = LocalDate.now();
                }
                DayOfWeek dayOfWeek = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dayOfWeek = currentDat.getDayOfWeek();
                }
                int dayOfWeekInt = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dayOfWeekInt = dayOfWeek.getValue();
                }
                long diffInMillies = today.getTime() - parsedDateWithoutTime.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diffInDays > 1) {
                    int y;
                    for (int x = (int) diffInDays; x >= 0; x--) {
                        y = x;
                        if (y > 7) y /= 7;
                        int index = Math.abs(dayOfWeekInt - y);
                        if (index < SettingsFragment.arrayOfTrainingDays.size() && SettingsFragment.arrayOfTrainingDays.get(index) == 1) {
                            homeWidgetValues.set(0, 0);
                            FileUtility.saveStats(requireContext(), "stats.json", homeWidgetValues);
                            break;
                        }
                    }
                }
            } else if (parsedDateWithoutTime.after(today)) {}
            else
                ifStreak = true;

            if(ifStreak && ifStreakFreeze)
                ifStreak = false;
            if (ifStreak)
                streak.setBackground(getResources().getDrawable(R.drawable.rounded_corners_pink_stroke));
            else
                streak.setBackground(getResources().getDrawable(R.drawable.rounded_corners_gray_stroke));
        }
        else
        {
            streak.setBackground(getResources().getDrawable(R.drawable.rounded_corners_blue_stroke));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = dateFormat.format(today);
            last_date.set(0, dateString);
            FileUtility.saveSettings(requireContext(), "last_date.json", last_date);
            ifStreakFreeze = true;
        }

        SpannableString spannableString = new SpannableString(getString(R.string.streak));
        RelativeSizeSpan smallerTextSize = new RelativeSizeSpan(0.8f);
        spannableString.setSpan(smallerTextSize, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan = new SpannableString(String.valueOf(homeWidgetValues.get(0)));
        numberSpan.setSpan(new RelativeSizeSpan(1.65f), 0, numberSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText = TextUtils.concat(spannableString, "\n", numberSpan);
        streak.setText(finalText);

        double hours = homeWidgetValues.get(1) / 3600.0;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String formattedHours = df.format(hours);
        SpannableString spannableString2 = new SpannableString(getString(R.string.hours_spent_exercising));
        RelativeSizeSpan smallerTextSize2 = new RelativeSizeSpan(0.8f);
        spannableString2.setSpan(smallerTextSize2, 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan2 = new SpannableString(formattedHours);
        numberSpan2.setSpan(new RelativeSizeSpan(1.65f), 0, numberSpan2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText2 = TextUtils.concat(spannableString2, "\n", numberSpan2);
        hours_spent_exercising.setText(finalText2);

        SpannableString spannableString3 = new SpannableString(getString(R.string.workouts_done));
        RelativeSizeSpan smallerTextSize3 = new RelativeSizeSpan(0.8f);
        spannableString3.setSpan(smallerTextSize3, 0, spannableString3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString numberSpan3 = new SpannableString(String.valueOf(homeWidgetValues.get(2)));
        numberSpan3.setSpan(new RelativeSizeSpan(1.65f), 0, numberSpan3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText3 = TextUtils.concat(spannableString3, "\n", numberSpan3);
        workouts_done.setText(finalText3);

        if(homeWidgetValues.get(3) >= 2147483647)
        {
            homeWidgetValues.set(3, 0);
            FileUtility.saveStats(requireContext(), "stats.json", homeWidgetValues);
            reps_failsafe.set(0, reps_failsafe.get(0) + 1);
            FileUtility.saveStats(requireContext(), "reps_failsafe.json", reps_failsafe);
        }
    }

    private void readFromExercisesArray() {
        exercisesMap = FileUtility.readWorkouts(requireContext(), "exercises.json");
        if (exercisesMap != null) {
            for (String key : exercisesMap.keySet()) {
                List<ArrayList<Object>> exercises = exercisesMap.get(key);
                ArrayList<WorkoutModel> workoutArray = new ArrayList<>();
                assert exercises != null;
                for (ArrayList<Object> exercise : exercises) {
                    String exerciseName = (String) exercise.get(0);
                    int repetitions = (int) exercise.get(1);
                    int sets = (int) exercise.get(2);
                    int weight = (int) exercise.get(3);
                    int difficulty = (int) exercise.get(4);
                    WorkoutModel t = new WorkoutModel(exerciseName, repetitions, sets, weight, difficulty);
                    workoutArray.add(t);
                }
                switch (key) {
                    case "abs":
                        absArray.addAll(workoutArray);
                        break;
                    case "chest":
                        chestArray.addAll(workoutArray);
                        break;
                    case "back":
                        backArray.addAll(workoutArray);
                        break;
                    case "shoulders":
                        shouldersArray.addAll(workoutArray);
                        break;
                    case "arms":
                        armsArray.addAll(workoutArray);
                        break;
                    case "legs":
                        legsArray.addAll(workoutArray);
                        break;
                    case "custom":
                        customArray.addAll(workoutArray);
                        break;
                }
            }
        }
    }

    private void readFromExercisesArray2() {
        exercisesMap3 = FileUtility.readWorkouts(requireContext(), "exercisesList.json");
        if (exercisesMap3 != null) {
            for (String key : exercisesMap3.keySet()) {
                List<ArrayList<Object>> exercises = exercisesMap3.get(key);
                ArrayList<WorkoutModel> workoutArray = new ArrayList<>();
                assert exercises != null;
                assert exercises != null;
                for (ArrayList<Object> exercise : exercises) {
                    String exerciseName = (String) exercise.get(0);
                    int repetitions = (int) exercise.get(1);
                    int sets = (int) exercise.get(2);
                    int weight = (int) exercise.get(3);
                    int difficulty = (int) exercise.get(4);
                    WorkoutModel t = new WorkoutModel(exerciseName, repetitions, sets, weight, difficulty);
                    workoutArray.add(t);
                }
                switch (key) {
                    case "abs":
                        absArrayList.addAll(workoutArray);
                        break;
                    case "chest":
                        chestArrayList.addAll(workoutArray);
                        break;
                    case "back":
                        backArrayList.addAll(workoutArray);
                        break;
                    case "shoulders":
                        shouldersArrayList.addAll(workoutArray);
                        break;
                    case "arms":
                        armsArrayList.addAll(workoutArray);
                        break;
                    case "legs":
                        legsArrayList.addAll(workoutArray);
                        break;

                }
            }
        }
    }

    private void readFromPersonalPlanArray() {
        try {
            String planArray = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){planArray = FileUtility.readPlan(requireContext(), "personalplan.json");}
            if (planArray != null) {
                JSONArray jsonArray = new JSONArray(planArray);
                int[][] arrays = new int[jsonArray.length()][];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray innerArray = jsonArray.getJSONArray(i);
                    arrays[i] = new int[innerArray.length()];
                    for (int j = 0; j < innerArray.length(); j++) {
                        arrays[i][j] = innerArray.getInt(j);
                    }
                }
                mondayArray = arrays[0];
                tuesdayArray = arrays[1];
                wednesdayArray = arrays[2];
                thursdayArray = arrays[3];
                fridayArray = arrays[4];
                saturdayArray = arrays[5];
                sundayArray = arrays[6];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}