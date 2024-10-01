package com.muscleup.muscleup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.muscleup.muscleup.ui.awards.ChallengeSession;
import com.muscleup.muscleup.ui.home.ExerciseSession;
import com.muscleup.muscleup.ui.home.HomeFragment;
import com.muscleup.muscleup.ui.home.SessionModel;
import com.muscleup.muscleup.ui.settings.DevicesFragment;
import com.muscleup.muscleup.ui.settings.SettingsFragment;
import com.muscleup.muscleup.ui.statistics.StatisticsFragment;
import com.muscleup.muscleup.ui.workouts.WorkoutModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Functions
{
    public static void sessionArrayFill(ArrayList<WorkoutModel> arrayEx, ArrayList<WorkoutModel> arrayEx2)
    {
        for (int j = arrayEx2.size() - 1; j >= 0; j--)
        {
            String value = arrayEx2.get(j).getName();
            if ((!SettingsFragment.isCheckedDumbbells && HomeFragment.dumbbellList.contains(value)) || (!SettingsFragment.isCheckedPullUps && HomeFragment.pullupList.contains(value))
                    || (!SettingsFragment.isCheckedDips && HomeFragment.dipList.contains(value)))
                arrayEx2.remove(j);
        }
        for (WorkoutModel exercise : arrayEx2)
        {
            String exerciseName = exercise.getName();
            int reps = exercise.getReps();
            int sets = exercise.getSets();
            int weight = exercise.getWeight();
            int difficulty = exercise.getDifficulty();
            for (int set = 1; set <= sets; set++) {
                String setName = (sets > 1) ? exerciseName + " (" + set + ")" : exerciseName;
                arrayEx.add(new WorkoutModel(setName, reps, sets, weight, difficulty));
            }
        }
    }

    public static void setText(ArrayList<WorkoutModel> arrayEx, ArrayList<WorkoutModel> arrayExCopy, ArrayList<SessionModel> statsFilled, TextView exercise_now, long startTime, int skip, int repsCounter, Context c)
    {
        try
        {
            WorkoutModel value = arrayEx.get(0);
            String t = c.getString(R.string.reps22);
            if(HomeFragment.arrayOfSeconds.contains(Functions.stripe(String.valueOf(value.getName())))) {
                t = c.getString(R.string.reps2_replacement2);
                HomeFragment.time = value.getReps();
            }
            else
                HomeFragment.time = 0;
            CharSequence finalText = TextUtils.concat(c.getString(R.string.now2) + " ", String.valueOf(value.getName()), "\n", t + " ", String.valueOf(value.getReps()));
            exercise_now.setText(finalText);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            exercise_now.setText(R.string.the_end);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            int durationInSeconds = (int) (duration / 1000);
            double form = ((double) skip / arrayExCopy.size()) * 100;
            int formm = 100 - (int) form;
            ExerciseSession.skip = 0; ChallengeSession.skip = 0; ExerciseSession.repsCounter = 0;
            ChallengeSession.repsCounter = 0; ExerciseSession.startTime = 0; ChallengeSession.startTime = 0;
            FileUtility.saveToSharedPreferences(c, "homewidgetvalues", HomeFragment.homeWidgetValues.get(0));
            afterSessionStats(formm, durationInSeconds, repsCounter, statsFilled, c);
        }
    }

    public static boolean containsOnlyZeros(int[] array)
    {
        for (int element : array)
        {
            if (element != 0)
                return false;
        }
        return true;
    }

    public static String stripe(String s)
    {
        if (s.contains("("))
            s = s.split("\\(")[0].trim();
        return s;
    }

    private static String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String buildSessionString(ArrayList<WorkoutModel> sessions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < sessions.size(); i++) {
            WorkoutModel session = sessions.get(i);
            sb.append("[\"").append(session.getName()).append("\", ").append(session.getReps()).append("]");
            if (i < sessions.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void send(String s){DevicesFragment.fragment.sending(s);}

    public static void afterSessionStats(int form, int durationInSeconds, int repsCounter, ArrayList<SessionModel> statsFilled, Context c)
    {
        StatisticsFragment.statsArray = statsFilled;
        HomeFragment.lastFormValue2 = true;
        ArrayList<SessionModel> temp;
        ArrayList<String> last_date = FileUtility.readSettings(c, "last_date.json");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        String todayString = dateFormat.format(today);

        if (!last_date.isEmpty() && last_date.get(0).equals(todayString)) {
            temp = FileUtility.readTodayStats(c, "todaystats.json");
            Map<String, SessionModel> mergedSessions = new HashMap<>();
            for (SessionModel session : temp)
                mergedSessions.put(session.getName(), session);

            for (SessionModel session : StatisticsFragment.statsArray)
            {
                String name = session.getName();
                if (mergedSessions.containsKey(name)) {
                    SessionModel existingSession = mergedSessions.get(name);
                    assert existingSession != null;
                    existingSession.setReps(existingSession.getReps() + session.getReps());
                } else
                    mergedSessions.put(name, session);
            }
            temp = new ArrayList<>(mergedSessions.values());
        }
        else
            temp = new ArrayList<>(StatisticsFragment.statsArray);
        FileUtility.saveTodayStats(c, "todaystats.json", temp);
        StatisticsFragment.saveFormValue(c, form);

        SharedPreferences sharedPreferences = c.getSharedPreferences("HomeFragmentPrefs", Context.MODE_PRIVATE);
        String lastExecutionDate = sharedPreferences.getString("LastExecutionDate", null);
        String currentDate = getCurrentDate();
        if (!currentDate.equals(lastExecutionDate))
        {
            HomeFragment.homeWidgetValues.set(0, HomeFragment.homeWidgetValues.get(0)+1);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("LastExecutionDate", currentDate);
            editor.apply();
        }

        String dateString = dateFormat.format(today);
        last_date.set(0, dateString);
        FileUtility.saveSettings(c, "last_date.json", last_date);
        HomeFragment.ifStreak = true;

        HomeFragment.homeWidgetValues.set(1, HomeFragment.homeWidgetValues.get(1)+durationInSeconds);
        HomeFragment.homeWidgetValues.set(2, HomeFragment.homeWidgetValues.get(2)+1);
        HomeFragment.homeWidgetValues.set(3, HomeFragment.homeWidgetValues.get(3)+repsCounter);
        FileUtility.saveStats(c, "stats.json", HomeFragment.homeWidgetValues);
        for (SessionModel session : StatisticsFragment.statsArray)
        {
            String name = session.getName();
            int reps = session.getReps();
            String nameLowerCase = name.toLowerCase();

            if(nameLowerCase.contains("push-ups"))
            {
                HomeFragment.unprocessedAwardsValues.set(0, HomeFragment.unprocessedAwardsValues.get(0) + reps);
                if (HomeFragment.unprocessedAwardsValues.get(0) >= 500)
                    HomeFragment.awardsArray.set(0, 1);
                if (HomeFragment.unprocessedAwardsValues.get(0) >= 1000)
                    HomeFragment.awardsArray.set(1, 1);
            }else if(nameLowerCase.contains("pull-ups"))
            {
                HomeFragment.unprocessedAwardsValues.set(1, HomeFragment.unprocessedAwardsValues.get(1) + reps);
                if (HomeFragment.unprocessedAwardsValues.get(1) >= 500)
                    HomeFragment.awardsArray.set(2, 1);
                if (HomeFragment.unprocessedAwardsValues.get(1) >= 1000)
                    HomeFragment.awardsArray.set(3, 1);
            }else if(nameLowerCase.contains("dips"))
            {
                HomeFragment.unprocessedAwardsValues.set(2, HomeFragment.unprocessedAwardsValues.get(2) + reps);
                if (HomeFragment.unprocessedAwardsValues.get(2) >= 500)
                    HomeFragment.awardsArray.set(4, 1);
                if (HomeFragment.unprocessedAwardsValues.get(2) >= 1000)
                    HomeFragment.awardsArray.set(5, 1);
            }

            if(HomeFragment.homeWidgetValues.get(2) >= 100)
                HomeFragment.awardsArray.set(6, 1);
            if(HomeFragment.homeWidgetValues.get(2) >= 365)
                HomeFragment.awardsArray.set(7, 1);
            double hours = HomeFragment.homeWidgetValues.get(1) / 3600.0;
            if(hours >= 100)
                HomeFragment.awardsArray.set(8, 1);
            if(hours >= 500)
                HomeFragment.awardsArray.set(9, 1);

            if(HomeFragment.ifChallenge)
            {
                HomeFragment.unprocessedAwardsValues.set(3, HomeFragment.unprocessedAwardsValues.get(3) + 1);
                if (HomeFragment.unprocessedAwardsValues.get(3) >= 50)
                    HomeFragment.awardsArray.set(10, 1);
                if (HomeFragment.unprocessedAwardsValues.get(3) >= 100)
                    HomeFragment.awardsArray.set(11, 1);
                HomeFragment.ifChallenge = false;
            }
            FileUtility.saveAwards(c, HomeFragment.unprocessedAwardsValues, "awardsStats.json");
            FileUtility.saveAwards(c, HomeFragment.awardsArray, "awards.json");
        }
    }

    public static String parseDifficulty(int x)
    {
        String t = "";
        if(x == 1)
            t = "easy";
        else if(x == 2)
            t = "medium";
        else
            t = "hard";
        return t;
    }

    public static Integer parseDifficultyRev(String x)
    {
        int t = 1;
        if(Objects.equals(x, "easy"))
            t = 1;
        else if(Objects.equals(x, "medium"))
            t = 2;
        else
            t = 3;
        return t;
    }

    public static void setUpSpinner(Context c, Spinner spinner, int arrayResourceId, AdapterView.OnItemSelectedListener listener) {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(c, arrayResourceId, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(listener);
    }

    public static void setUpSpinner2(Context c, Spinner spinner, ArrayList<String> array)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public static String translateDifficulty(String difficulty)
    {
        switch (difficulty) {
            case "łatwy":
                difficulty = "easy";
                break;
            case "średni":
                difficulty = "medium";
                break;
            case "trudny":
                difficulty = "hard";
                break;
        }
        return difficulty;
    }

    public static String isDefaultExercise(String name)
    {
        for (String key : HomeFragment.exercisesMap3.keySet()) {
            ArrayList<ArrayList<Object>> exercises = HomeFragment.exercisesMap3.get(key);
            for (ArrayList<Object> exercise : exercises) {
                if(exercise.get(0).equals(name)) {
                    return exercise.get(1) + " " + exercise.get(2);
                }
            }
        }
        return "0";
    }
}
