package com.example.medication_reminder_app.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.medication_reminder_app.data.dao.MedicineDao;
import com.example.medication_reminder_app.data.dao.ScheduleDao;
import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.utils.AlarmUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final ScheduleDao scheduleDao;
    private final ExecutorService executor;

    public interface OnSaveCallback {
        void onSaved(long medicineId, List<Schedule> savedSchedules);
    }

    public MedicineRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.medicineDao = db.medicineDao();
        this.scheduleDao = db.scheduleDao();
        this.executor = Executors.newFixedThreadPool(2);
    }

    // Thêm thuốc + lịch — trả về schedules với id thật
    public void addMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                         OnSaveCallback callback) {
        executor.execute(() -> {
            long medicineId = medicineDao.insert(medicine);
            for (Schedule s : schedules) {
                s.medicineId = (int) medicineId;
            }
            List<Long> ids = scheduleDao.insertAll(schedules);
            for (int i = 0; i < schedules.size(); i++) {
                schedules.get(i).id = ids.get(i).intValue();
            }
            if (callback != null) {
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onSaved(medicineId, schedules));
            }
        });
    }

    // Sửa thuốc — xóa lịch cũ, thêm lịch mới với id thật
    public void updateMedicineWithSchedules(Medicine medicine, List<Schedule> newSchedules,
                                            OnSaveCallback callback) {
        executor.execute(() -> {
            medicineDao.update(medicine);
            scheduleDao.deleteByMedicineId(medicine.id);
            for (Schedule s : newSchedules) {
                s.medicineId = medicine.id;
            }
            List<Long> ids = scheduleDao.insertAll(newSchedules);
            for (int i = 0; i < newSchedules.size(); i++) {
                newSchedules.get(i).id = ids.get(i).intValue();
            }
            if (callback != null) {
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onSaved(medicine.id, newSchedules));
            }
        });
    }

    // Xóa mềm + cancel tất cả alarm
    public void deactivateMedicine(Context context, int medicineId) {
        executor.execute(() -> {
            List<Schedule> schedules = scheduleDao.getByMedicineIdSync(medicineId);
            for (Schedule s : schedules) {
                AlarmUtils.cancelAlarm(context, s.id);
            }
            medicineDao.setActive(medicineId, false);
            scheduleDao.deactivateByMedicine(medicineId);
        });
    }

    public void updateImagePath(int medicineId, String imagePath) {
        executor.execute(() -> medicineDao.updateImagePath(medicineId, imagePath));
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

    // Sync cho background service
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