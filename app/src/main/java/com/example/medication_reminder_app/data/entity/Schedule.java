package com.example.medication_reminder_app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "schedules",
        foreignKeys = @ForeignKey(
                entity = Medicine.class,
                parentColumns = "id",
                childColumns = "medicine_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("medicine_id")}
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
    public boolean isActive = true;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public Schedule(int medicineId, int timeHour, int timeMinute) {
        this.medicineId = medicineId;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTimeString() {
        return String.format("%02d:%02d", timeHour, timeMinute);
    }

    public int getTotalMinutes() {
        return timeHour * 60 + timeMinute;
    }
}