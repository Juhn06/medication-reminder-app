package com.example.medication_reminder_app.ui.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medication_reminder_app.R;

public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor, accelSensor;
    private TextView tvLight, tvStepCount, tvStepAdvice;
    private int stepCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLight     = view.findViewById(R.id.tvLight);
        tvStepCount = view.findViewById(R.id.tvStepCount);
        tvStepAdvice = view.findViewById(R.id.tvStepAdvice);

        TextView tvTemperature = view.findViewById(R.id.tvTemperature);
        TextView tvPressure    = view.findViewById(R.id.tvPressure);
        tvTemperature.setText("Không hỗ trợ");
        tvPressure.setText("Không hỗ trợ");

        sensorManager = (SensorManager) requireActivity()
                .getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (accelSensor != null)
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            tvLight.setText(String.format("%.0f lux", event.values[0]));
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            float force = (float) Math.sqrt(x*x + y*y + z*z);
            if (force > 12f && force < 20f) {
                stepCount++;
                tvStepCount.setText(stepCount + " bước chân hôm nay");
                if (stepCount < 1000) tvStepAdvice.setText("Hãy đi bộ nhẹ 15 phút!");
                else if (stepCount < 5000) tvStepAdvice.setText("Tốt! Tiếp tục vận động!");
                else tvStepAdvice.setText("Xuất sắc! Rất năng động!");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}