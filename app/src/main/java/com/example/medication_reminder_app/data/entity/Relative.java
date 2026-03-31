package com.example.medication_reminder_app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "relatives")
public class Relative {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "fcm_token")
    public String fcmToken;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public Relative(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.createdAt = System.currentTimeMillis();
    }
}