package com.muscleup.muscleup.ui.home;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SessionModel
{
    private final String name;
    private Integer reps;

    public SessionModel(String name, Integer reps)
    {
        this.name = name;
        this.reps = reps;
    }

    @NonNull
    @Override
    public String toString(){return "['" + name + "'," + reps + "]";}

    public String getName(){return name;}
    public Integer getReps(){return reps;}
    public void setReps(Integer reps){this.reps = reps;}

    public static Integer getRepsByName(ArrayList<SessionModel> sessionList, String name) {
        for (SessionModel session : sessionList) {
            if (session.getName().equals(name))
                return session.getReps();
        }
        return null;
    }

    public static SessionModel fromJson(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        int reps = jsonObject.getInt("reps");
        return new SessionModel(name, reps);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("reps", reps);
        return jsonObject;
    }
}
