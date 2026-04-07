package com.example.medication_reminder_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.data.repository.MedicineRepository;

import java.util.List;
import java.util.function.Consumer;

public class MedicineViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> allMedicines;

    public MedicineViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        allMedicines = repository.getAllMedicines();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Schedule>> getSchedulesForMedicine(int medicineId) {
        return repository.getSchedulesForMedicine(medicineId);
    }

    public void addMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                          Consumer<Long> onSuccess) {
        repository.addMedicineWithSchedules(medicine, schedules, onSuccess);
    }

    public void deleteMedicine(Medicine medicine) {
        repository.deleteMedicine(medicine);
    }

    public void updateImagePath(int medicineId, String path) {
        repository.updateImagePath(medicineId, path);
    }
}
