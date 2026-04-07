package com.example.medication_reminder_app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.medication_reminder_app.data.dao.MedicineDao;
import com.example.medication_reminder_app.data.dao.ScheduleDao;
import com.example.medication_reminder_app.data.database.AppDatabase;
import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final ScheduleDao scheduleDao;
    private final LiveData<List<Medicine>> allMedicines;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MedicineRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        medicineDao = db.medicineDao();
        scheduleDao = db.scheduleDao();
        allMedicines = medicineDao.getAllMedicines();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Schedule>> getSchedulesForMedicine(int medicineId) {
        return scheduleDao.getSchedulesForMedicine(medicineId);
    }

    /**
     * Thêm thuốc + lịch uống trong một transaction.
     * Callback trả về ID của thuốc vừa tạo, chạy trên main thread.
     */
    public void addMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                         Consumer<Long> onSuccess) {
        executor.execute(() -> {
            long medicineId = medicineDao.insert(medicine);
            for (Schedule s : schedules) {
                s.medicineId = (int) medicineId;
                scheduleDao.insert(s);
            }
            if (onSuccess != null) {
                android.os.Handler mainHandler = new android.os.Handler(
                        android.os.Looper.getMainLooper());
                mainHandler.post(() -> onSuccess.accept(medicineId));
            }
        });
    }

    public void deleteMedicine(Medicine medicine) {
        executor.execute(() -> medicineDao.delete(medicine));
    }

    public void updateImagePath(int medicineId, String path) {
        executor.execute(() -> medicineDao.updateImagePath(medicineId, path));
    }
}
