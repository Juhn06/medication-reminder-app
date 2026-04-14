package com.example.medication_reminder_app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "history_logs",
        foreignKeys = {
                @ForeignKey(
                        entity = Medicine.class,
                        parentColumns = "id",
                        childColumns = "medicine_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index("medicine_id"),
                @Index("scheduled_date")
        }
)
public class HistoryLog {

    public static final String STATUS_TAKEN   = "TAKEN";
    public static final String STATUS_MISSED  = "MISSED";
    public static final String STATUS_SNOOZED = "SNOOZED";

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "medicine_id")
    public Integer medicineId;

    @ColumnInfo(name = "medicine_name")
    public String medicineName;

    @ColumnInfo(name = "medicine_dosage")
    public String medicineDosage;

    @ColumnInfo(name = "schedule_id")
    public int scheduleId;

    @ColumnInfo(name = "scheduled_time")
    public long scheduledTime;

    @ColumnInfo(name = "scheduled_date")
    public String scheduledDate;

    @ColumnInfo(name = "taken_time")
    public long takenTime;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "snooze_first_time", defaultValue = "0")
    public long snoozeFirstTime; // thời điểm snooze đầu tiên, 0 nếu chưa snooze

    public HistoryLog(int scheduleId, Integer medicineId, String medicineName,
                      String medicineDosage, long scheduledTime, String status) {
        this.scheduleId = scheduleId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicineDosage = medicineDosage;
        this.scheduledTime = scheduledTime;
        this.scheduledDate = DateUtils.toDateString(scheduledTime);
        this.status = status;
        this.takenTime = STATUS_TAKEN.equals(status) ? System.currentTimeMillis() : 0;
        this.snoozeFirstTime = 0;
    }

    public boolean isTaken()   { return STATUS_TAKEN.equals(status); }
    public boolean isMissed()  { return STATUS_MISSED.equals(status); }
    public boolean isSnoozed() { return STATUS_SNOOZED.equals(status); }
}