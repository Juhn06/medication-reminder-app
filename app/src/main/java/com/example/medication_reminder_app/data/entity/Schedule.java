package com.example.medication_reminder_app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Locale;

@Entity(
    tableName = "schedules",
    foreignKeys = @ForeignKey(
        entity = Medicine.class,
        parentColumns = "id",
        childColumns = "medicine_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index(value = {"medicine_id"})
)
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "medicine_id")
    public int medicineId;

    @ColumnInfo(name = "time_hour")
    public int timeHour;

    @ColumnInfo(name = "time_minute")
    public int timeMinute;

    @ColumnInfo(name = "is_active")
    public boolean isActive;

    public Schedule(int medicineId, int timeHour, int timeMinute) {
        this.medicineId = medicineId;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.isActive = true;
    }

    public String getTimeString() {
        return String.format(Locale.getDefault(), "%02d:%02d", timeHour, timeMinute);
    }
}
