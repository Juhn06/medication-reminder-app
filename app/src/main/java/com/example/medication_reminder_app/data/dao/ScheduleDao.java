package com.example.medication_reminder_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medication_reminder_app.data.entity.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    long insert(Schedule schedule);

    @Delete
    void delete(Schedule schedule);

    @Query("SELECT * FROM schedules WHERE medicine_id = :medicineId ORDER BY time_hour, time_minute")
    LiveData<List<Schedule>> getSchedulesForMedicine(int medicineId);

    @Query("DELETE FROM schedules WHERE medicine_id = :medicineId")
    void deleteSchedulesForMedicine(int medicineId);
}
