package com.example.medication_reminder_app.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.medication_reminder_app.data.dao.MedicineDao;
import com.example.medication_reminder_app.data.dao.ScheduleDao;
import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final ScheduleDao scheduleDao;
    private final ExecutorService executor;

    public interface OnInsertCallback {
        void onInserted(long medicineId);
    }

    public MedicineRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.medicineDao = db.medicineDao();
        this.scheduleDao = db.scheduleDao();
        this.executor = Executors.newFixedThreadPool(2);
    }

    // Thêm thuốc + lịch uống cùng lúc
    public void addMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                         OnInsertCallback callback) {
        executor.execute(() -> {
            long medicineId = medicineDao.insert(medicine);
            for (Schedule s : schedules) {
                s.medicineId = (int) medicineId;
            }
            scheduleDao.insertAll(schedules);
            if (callback != null) {
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onInserted(medicineId));
            }
        });
    }

    // Cập nhật thuốc + thay lịch hoàn toàn
    public void updateMedicineWithSchedules(Medicine medicine, List<Schedule> newSchedules,
                                            Runnable onComplete) {
        executor.execute(() -> {
            medicineDao.update(medicine);
            scheduleDao.deleteByMedicineId(medicine.id);
            for (Schedule s : newSchedules) {
                s.medicineId = medicine.id;
            }
            scheduleDao.insertAll(newSchedules);
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }

    // Xóa mềm thuốc
    public void deactivateMedicine(int medicineId) {
        executor.execute(() -> {
            medicineDao.setActive(medicineId, false);
            scheduleDao.deactivateByMedicine(medicineId);
        });
    }

    public void deleteMedicine(Medicine medicine) {
        executor.execute(() -> medicineDao.delete(medicine));
    }

    // TV3 dùng: cập nhật ảnh
    public void updateImagePath(int medicineId, String imagePath) {
        executor.execute(() -> medicineDao.updateImagePath(medicineId, imagePath));
    }

    public void setScheduleActive(int scheduleId, boolean isActive) {
        executor.execute(() -> scheduleDao.setActive(scheduleId, isActive));
    }

    // LiveData cho UI
    public LiveData<List<Medicine>> getAllActiveMedicines() {
        return medicineDao.getAllActive();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return medicineDao.getAll();
    }

    public LiveData<List<Schedule>> getSchedulesByMedicine(int medicineId) {
        return scheduleDao.getByMedicineId(medicineId);
    }

    // Sync cho background service (TV2, TV4)
    public Medicine getMedicineByIdSync(int id) {
        return medicineDao.getByIdSync(id);
    }

    public List<Medicine> getAllActiveMedicinesSync() {
        return medicineDao.getAllActiveSync();
    }

    public List<Schedule> getSchedulesByMedicineSync(int medicineId) {
        return scheduleDao.getByMedicineIdSync(medicineId);
    }

    public List<Schedule> getAllActiveSchedulesSync() {
        return scheduleDao.getAllActiveSync();
    }
}