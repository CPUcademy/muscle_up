package com.muscleup.muscleup.ui.workouts;

import androidx.annotation.NonNull;

public class WorkoutModel
{
    private String name;
    private int reps;
    private int sets;
    private int weight;
    private int difficulty;

    public WorkoutModel(String name, int reps, int sets, int weight, int difficulty) {
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.weight = weight;
        this.difficulty = difficulty;
    }

    @NonNull
    @Override
    public String toString(){return "['" + name + "'," + reps + "," + sets + "," + weight + "," + difficulty + "]";}

    public String getName(){return name;}
    public Integer getReps(){return reps;}
    public Integer getSets(){return sets;}
    public Integer getWeight(){return weight;}
    public Integer getDifficulty(){return difficulty;}

    public void setName(String name){this.name = name;}
    public void setReps(int reps){this.reps = reps;}
    public void setSets(int sets){this.sets = sets;}
    public void setWeight(int weight){this.weight = weight;}
    public void setDifficulty(int difficulty){this.difficulty = difficulty;}
}
