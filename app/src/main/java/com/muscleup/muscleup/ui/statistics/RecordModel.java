package com.muscleup.muscleup.ui.statistics;

import androidx.annotation.NonNull;

public class RecordModel
{
    private final String name;
    private String number;

    public RecordModel(String name, String number)
    {
        this.name = name;
        this.number = number;
    }

    @NonNull
    @Override
    public String toString(){return "'" + name + "': '" + number + "'";}

    public String getName(){return name;}
    public String getNumber(){return number;}

    public void setNumber(String number){this.number = number;}
}
