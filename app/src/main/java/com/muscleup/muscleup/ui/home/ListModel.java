package com.muscleup.muscleup.ui.home;

import androidx.annotation.NonNull;

public class ListModel

{
    private String name;
    private String difficulty;
    private String muscleGroup;

    public ListModel(String name, String difficulty, String muscleGroup)
    {
        this.name = name;
        this.difficulty = difficulty;
        this.muscleGroup = muscleGroup;
    }

    @NonNull
    @Override
    public String toString(){return "['" + name + "'," + difficulty + "," + muscleGroup + "]";}

    public String getName(){return name;}
    public String getDifficulty(){return difficulty;}
    public String getMuscleGroup(){return muscleGroup;}

    public void setName(String name){this.name = name;}
    public void setDifficulty(String difficulty){this.difficulty = difficulty;}
    public void setMuscleGroup(String muscleGroup){this.muscleGroup = muscleGroup;}
}
