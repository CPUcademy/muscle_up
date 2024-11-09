package com.muscleup.muscleup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muscleup.muscleup.ui.home.SessionModel;
import com.muscleup.muscleup.ui.settings.SettingsFragment;
import com.muscleup.muscleup.ui.statistics.RecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtility
{
    public static void copyFileToInternalStorage(Context context, String FILE_NAME) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if ((!file.exists() && !SettingsFragment.defaultSettings) || (file.exists() && SettingsFragment.defaultSettings)) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(FILE_NAME);
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, ArrayList<ArrayList<Object>>> readWorkouts(Context context, String FILE_NAME) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(context.getFilesDir(), FILE_NAME);
            return objectMapper.readValue(file, new TypeReference<HashMap<String, ArrayList<ArrayList<Object>>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveWorkouts(Context context, HashMap<String, ArrayList<ArrayList<Object>>> exercisesMap, String FILE_NAME) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(context.getFilesDir(), FILE_NAME);
            objectMapper.writeValue(file, exercisesMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String readPlan(Context context, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void savePlan(Context context, String fileName, String data) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            Files.write(file.toPath(), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> readAwards(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        String dataString = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            dataString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dataString == null)
            return new ArrayList<>();
        ArrayList<Integer> data = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(dataString);
            for (int i = 0; i < jsonArray.length(); i++) {
                data.add(jsonArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveAwards(Context context, ArrayList<Integer> data, String fileName) {
        StringBuilder dataStringBuilder = new StringBuilder();
        dataStringBuilder.append("[");
        for (int i = 0; i < data.size(); i++) {
            dataStringBuilder.append(data.get(i));
            if (i < data.size() - 1) {
                dataStringBuilder.append(", ");
            }
        }
        dataStringBuilder.append("]");
        String dataString = dataStringBuilder.toString();
        File file = new File(context.getFilesDir(), fileName);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(dataString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<RecordModel> readRecords(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        String dataString = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            dataString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dataString == null)
            return new ArrayList<>();
        ArrayList<RecordModel> data = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(dataString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray innerArray = jsonArray.getJSONArray(i);
                String name = innerArray.getString(0);
                String number = innerArray.getString(1);
                RecordModel recordModel = new RecordModel(name, number);
                data.add(recordModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveRecords(Context context, String fileName, ArrayList<RecordModel> records) {
        File file = new File(context.getFilesDir(), fileName);
        JSONArray jsonArray = new JSONArray();

        for (RecordModel record : records) {
            JSONArray innerArray = new JSONArray();
            innerArray.put(record.getName());
            innerArray.put(record.getNumber());
            jsonArray.put(innerArray);
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readSettings(Context context, String fileName) {
        ArrayList<String> dataList = new ArrayList<>();
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataList.add(jsonArray.getString(i));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public static void saveSettings(Context context, String fileName, ArrayList<String> dataList) {
        JSONArray jsonArray = new JSONArray(dataList);
        File file = new File(context.getFilesDir(), fileName);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(jsonArray.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> readStats(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        String dataString = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            dataString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dataString == null)
            return new ArrayList<>();
        ArrayList<Integer> data = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(dataString);
            for (int i = 0; i < jsonArray.length(); i++) {
                data.add(jsonArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveStats(Context context, String fileName, ArrayList<Integer> data) {
        File file = new File(context.getFilesDir(), fileName);
        JSONArray jsonArray = new JSONArray();

        for (int value : data) {
            jsonArray.put(value);
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SessionModel> readTodayStats(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        String dataString = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            dataString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<SessionModel> statsArray = new ArrayList<>();

        if (dataString != null) {
            try {
                JSONArray jsonArray = new JSONArray(dataString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    statsArray.add(SessionModel.fromJson(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return statsArray;
    }

    public static void saveTodayStats(Context context, String fileName, ArrayList<SessionModel> statsArray) {
        File file = new File(context.getFilesDir(), fileName);
        JSONArray jsonArray = new JSONArray();
        try {
            for (SessionModel session : statsArray)
                jsonArray.put(session.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToSharedPreferences(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.muscleup.muscleup", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static HashMap<String, String> readHashMap(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<HashMap<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void saveHashMap(Context context, HashMap<String, String> hashMap, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, hashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
