package com.example.medication_reminder_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.medication_reminder_app.data.entity.Medicine;

import java.util.List;

@Dao
public interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Medicine medicine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Medicine> medicines);

    @Update
    void update(Medicine medicine);

    @Query("UPDATE medicines SET image_path = :imagePath WHERE id = :medicineId")
    void updateImagePath(int medicineId, String imagePath);

    @Query("UPDATE medicines SET is_active = :isActive WHERE id = :medicineId")
    void setActive(int medicineId, boolean isActive);

    @Delete
    void delete(Medicine medicine);

    @Query("DELETE FROM medicines WHERE id = :medicineId")
    void deleteById(int medicineId);

    @Query("DELETE FROM medicines")
    void deleteAll();

    @Query("SELECT * FROM medicines WHERE is_active = 1 ORDER BY name ASC")
    LiveData<List<Medicine>> getAllActive();

    @Query("SELECT * FROM medicines ORDER BY created_at DESC")
    LiveData<List<Medicine>> getAll();

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    Medicine getByIdSync(int id);

    @Query("SELECT * FROM medicines WHERE is_active = 1")
    List<Medicine> getAllActiveSync();

    @Query("SELECT COUNT(*) FROM medicines WHERE is_active = 1")
    int countActive();
}