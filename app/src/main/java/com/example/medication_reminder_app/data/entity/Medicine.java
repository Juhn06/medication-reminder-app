package com.example.medication_reminder_app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "medicines")
public class Medicine {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "dosage")
    public String dosage;

    @ColumnInfo(name = "times_per_day")
    public int timesPerDay;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "image_path")
    public String imagePath;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "is_active")
    public boolean isActive = true;

    public Medicine(String name, String dosage, int timesPerDay, String notes) {
        this.name = name;
        this.dosage = dosage;
        this.timesPerDay = timesPerDay;
        this.notes = notes;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    public String getDisplayName() {
        return name + " - " + dosage;
    }
}