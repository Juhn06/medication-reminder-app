package com.example.medication_reminder_app.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.medication_reminder_app.data.entity.Medicine;
import com.example.medication_reminder_app.data.entity.Schedule;
import com.example.medication_reminder_app.data.repository.MedicineRepository;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> allActiveMedicines;
    private final MutableLiveData<String> saveStatus = new MutableLiveData<>();

    public MedicineViewModel(Application application) {
        super(application);
        repository = new MedicineRepository(application);
        allActiveMedicines = repository.getAllActiveMedicines();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allActiveMedicines;
    }

    public LiveData<String> getSaveStatus() {
        return saveStatus;
    }

    public void addMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                         MedicineRepository.OnSaveCallback callback) {
        repository.addMedicineWithSchedules(medicine, schedules, (id, savedSchedules) -> {
            saveStatus.postValue("SUCCESS:" + id);
            if (callback != null) callback.onSaved(id, savedSchedules);
        });
    }

    public void updateMedicineWithSchedules(Medicine medicine, List<Schedule> schedules,
                                            MedicineRepository.OnSaveCallback callback) {
        repository.updateMedicineWithSchedules(medicine, schedules, (id, savedSchedules) -> {
            saveStatus.postValue("UPDATED");
            if (callback != null) callback.onSaved(id, savedSchedules);
        });
    }

    public void deleteMedicine(Context context, Medicine medicine) {
        repository.deactivateMedicine(context, medicine.id);
    }

    public void updateImagePath(int medicineId, String imagePath) {
        repository.updateImagePath(medicineId, imagePath);
    }

    public LiveData<List<Schedule>> getSchedulesForMedicine(int medicineId) {
        return repository.getSchedulesByMedicine(medicineId);
    }
}