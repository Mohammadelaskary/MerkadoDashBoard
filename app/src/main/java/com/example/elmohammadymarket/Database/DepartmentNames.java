package com.example.elmohammadymarket.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DepartmentNames {
    @PrimaryKey
    @NonNull
    private String depName;

    public DepartmentNames() {
    }


    @NonNull
    public String getDepName() {
        return depName;
    }

    public void setDepName(@NonNull String depName) {
        this.depName = depName;
    }
}
