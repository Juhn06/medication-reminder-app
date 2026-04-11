package com.example.myapplicationmedtech;

public class MedicineScheduleItem {
    private final String medicineName;
    private final String time;
    private final String status;

    public MedicineScheduleItem(String medicineName, String time, String status) {
        this.medicineName = medicineName;
        this.time = time;
        this.status = status;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}

