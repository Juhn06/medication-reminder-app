package com.example.medication_reminder_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.medication_reminder_app.data.entity.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Schedule schedule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Schedule> schedules);

    @Update
    void update(Schedule schedule);

    @Query("UPDATE schedules SET is_active = :isActive WHERE id = :scheduleId")
    void setActive(int scheduleId, boolean isActive);

    @Query("UPDATE schedules SET is_active = 0 WHERE medicine_id = :medicineId")
    void deactivateByMedicine(int medicineId);

    @Delete
    void delete(Schedule schedule);

    @Query("DELETE FROM schedules WHERE id = :scheduleId")
    void deleteById(int scheduleId);

    @Query("DELETE FROM schedules WHERE medicine_id = :medicineId")
    void deleteByMedicineId(int medicineId);

    @Query("SELECT * FROM schedules WHERE medicine_id = :medicineId ORDER BY time_hour, time_minute")
    LiveData<List<Schedule>> getByMedicineId(int medicineId);

    @Query("SELECT * FROM schedules WHERE is_active = 1 ORDER BY time_hour, time_minute")
    LiveData<List<Schedule>> getAllActive();

    @Query("SELECT * FROM schedules WHERE medicine_id = :medicineId AND is_active = 1")
    List<Schedule> getByMedicineIdSync(int medicineId);

    @Query("SELECT * FROM schedules WHERE is_active = 1")
    List<Schedule> getAllActiveSync();

    @Query("SELECT * FROM schedules WHERE id = :scheduleId LIMIT 1")
    Schedule getByIdSync(int scheduleId);

    @Query("SELECT COUNT(*) FROM schedules WHERE medicine_id = :medicineId AND is_active = 1")
    int countByMedicine(int medicineId);
}