package com.example.theja.sythcontroller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private TextView t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gyro values
        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);
        mTextView3 = findViewById(R.id.textView3);

        // Rotvec values
        mTextView4 = findViewById(R.id.textView4);
        mTextView5 = findViewById(R.id.textView5);
        mTextView6 = findViewById(R.id.textView6);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rotvec = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        SensorEventListener gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                mTextView1.setText(String.format("X: %s", String.format(Locale.getDefault(), "%.2f", event.values[0])));
                mTextView2.setText(String.format("Y: %s", String.format(Locale.getDefault(), "%.2f", event.values[1])));
                mTextView3.setText(String.format("Z: %s", String.format(Locale.getDefault(), "%.2f", event.values[2])));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        SensorEventListener rotvecListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                // Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                mTextView4.setText(String.format("X: %s°", String.format(Locale.getDefault(), "%.2f", orientations[0])));
                mTextView5.setText(String.format("Y: %s°", String.format(Locale.getDefault(), "%.2f", orientations[1])));
                mTextView6.setText(String.format("Z: %s°", String.format(Locale.getDefault(), "%.2f", orientations[2])));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotvecListener, rotvec, SensorManager.SENSOR_DELAY_NORMAL);

        // Enables Always-on
        setAmbientEnabled();
    }

     public void onButtonClicked(View target) {
        t3.setText("Button clicked");
     }
}