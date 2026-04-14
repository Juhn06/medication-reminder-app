package com.example.medication_reminder_app.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.medication_reminder_app.data.dao.HistoryDao;
import com.example.medication_reminder_app.data.dao.MedicineDao;
import com.example.medication_reminder_app.data.dao.ScheduleDao;
import com.example.medication_reminder_app.data.entity.HistoryLog;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;

@Database(
        entities = {
                Medicine.class,
                Schedule.class,
                HistoryLog.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "med_reminder.db";
    private static volatile AppDatabase INSTANCE;

    public abstract MedicineDao medicineDao();
    public abstract ScheduleDao scheduleDao();
    public abstract HistoryDao  historyDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}