package com.example.medication_reminder_app.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.medication_reminder_app.data.dao.HistoryDao;
import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.DateUtils;
import com.example.medication_reminder_app.data.entity.HistoryLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryRepository {

    private final HistoryDao historyDao;
    private final ExecutorService executor;

    public HistoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.historyDao = db.historyDao();
        this.executor = Executors.newSingleThreadExecutor();
    }


    public void insertLog(HistoryLog log) {
        executor.execute(() -> historyDao.insert(log));
    }


    public void markTaken(int scheduleId, String date) {
        executor.execute(() -> {
            long now = System.currentTimeMillis();
            HistoryLog existing = historyDao.getByScheduleAndDate(scheduleId, date);
            if (existing != null) {
                historyDao.updateStatus(existing.id, HistoryLog.STATUS_TAKEN, now);
            }
        });
    }

    public void updateStatus(int logId, String status) {
        executor.execute(() -> {
            long takenTime = HistoryLog.STATUS_TAKEN.equals(status)
                    ? System.currentTimeMillis() : 0;
            historyDao.updateStatus(logId, status, takenTime);
        });
    }


    public LiveData<List<HistoryLog>> getToday() {
        return historyDao.getToday(DateUtils.today());
    }

    public LiveData<List<HistoryLog>> getByDate(String date) {
        return historyDao.getByDate(date);
    }

    public LiveData<List<HistoryLog>> getByDateRange(String fromDate, String toDate) {
        return historyDao.getByDateRange(fromDate, toDate);
    }

    public LiveData<List<HistoryLog>> getByMedicine(int medicineId) {
        return historyDao.getByMedicine(medicineId);
    }


    public LiveData<Integer> getTakenCount(String fromDate, String toDate) {
        return historyDao.getTakenCount(fromDate, toDate);
    }

    public LiveData<Integer> getTotalCount(String fromDate, String toDate) {
        return historyDao.getTotalCount(fromDate, toDate);
    }


    public float getComplianceRateSync(String fromDate, String toDate) {
        int total = historyDao.getTotalCountSync(fromDate, toDate);
        int taken = historyDao.getTakenCountSync(fromDate, toDate);
        if (total == 0) return 0f;
        return (taken * 100f) / total;
    }
}