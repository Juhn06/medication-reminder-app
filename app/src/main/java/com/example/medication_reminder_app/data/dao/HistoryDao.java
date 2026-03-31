package com.example.medication_reminder_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.medication_reminder_app.data.entity.HistoryLog;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HistoryLog log);

    @Update
    void update(HistoryLog log);

    @Query("UPDATE history_logs SET status = :status, taken_time = :takenTime WHERE id = :logId")
    void updateStatus(int logId, String status, long takenTime);

    @Query("UPDATE history_logs SET status = 'TAKEN', taken_time = :takenTime " +
            "WHERE schedule_id = :scheduleId AND scheduled_date = :date")
    void markTaken(int scheduleId, String date, long takenTime);

    @Delete
    void delete(HistoryLog log);

    @Query("DELETE FROM history_logs WHERE scheduled_date < :beforeDate")
    void deleteOlderThan(String beforeDate);

    @Query("SELECT * FROM history_logs WHERE scheduled_date = :date ORDER BY scheduled_time ASC")
    LiveData<List<HistoryLog>> getByDate(String date);

    @Query("SELECT * FROM history_logs WHERE scheduled_date = :today ORDER BY scheduled_time ASC")
    LiveData<List<HistoryLog>> getToday(String today);

    @Query("SELECT * FROM history_logs WHERE scheduled_date = :date AND status = :status ORDER BY scheduled_time ASC")
    LiveData<List<HistoryLog>> getByDateAndStatus(String date, String status);

    @Query("SELECT * FROM history_logs WHERE medicine_id = :medicineId ORDER BY scheduled_time DESC")
    LiveData<List<HistoryLog>> getByMedicine(int medicineId);

    @Query("SELECT COUNT(*) FROM history_logs " +
            "WHERE scheduled_date BETWEEN :fromDate AND :toDate AND status = 'TAKEN'")
    LiveData<Integer> getTakenCount(String fromDate, String toDate);

    @Query("SELECT COUNT(*) FROM history_logs " +
            "WHERE scheduled_date BETWEEN :fromDate AND :toDate")
    LiveData<Integer> getTotalCount(String fromDate, String toDate);

    @Query("SELECT * FROM history_logs " +
            "WHERE scheduled_date BETWEEN :fromDate AND :toDate " +
            "ORDER BY scheduled_date ASC, scheduled_time ASC")
    LiveData<List<HistoryLog>> getByDateRange(String fromDate, String toDate);

    @Query("SELECT * FROM history_logs " +
            "WHERE schedule_id = :scheduleId AND scheduled_date = :date LIMIT 1")
    HistoryLog getByScheduleAndDate(int scheduleId, String date);

    @Query("SELECT COUNT(*) FROM history_logs " +
            "WHERE scheduled_date BETWEEN :fromDate AND :toDate AND status = 'TAKEN'")
    int getTakenCountSync(String fromDate, String toDate);

    @Query("SELECT COUNT(*) FROM history_logs " +
            "WHERE scheduled_date BETWEEN :fromDate AND :toDate")
    int getTotalCountSync(String fromDate, String toDate);
}