package com.example.medication_reminder_app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.medication_reminder_app.data.entity.Relative;

import java.util.List;

@Dao
public interface RelativeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Relative relative);

    @Update
    void update(Relative relative);

    @Delete
    void delete(Relative relative);

    @Query("DELETE FROM relatives WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM relatives ORDER BY name ASC")
    LiveData<List<Relative>> getAll();

    @Query("SELECT * FROM relatives")
    List<Relative> getAllSync();

    @Query("UPDATE relatives SET fcm_token = :token WHERE id = :relativeId")
    void updateFcmToken(int relativeId, String token);
}