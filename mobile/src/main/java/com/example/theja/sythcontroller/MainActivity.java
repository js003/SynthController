package com.example.theja.sythcontroller;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientationAngles = new float[3];
    SensorEventListener accelerometerListener;
    SensorEventListener magneticFieldListener;
    String oldvalY = "";

    // Low pass filter
    static final float ALPHA = 0.1f;

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private TextView mTextView7;
    private Switch mSwitch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //System.arraycopy(event.values, 0, mAccelerometerReading,0, mAccelerometerReading.length);
                mAccelerometerReading = lowPass(event.values.clone(), mAccelerometerReading);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        magneticFieldListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //System.arraycopy(event.values, 0, mMagnetometerReading,0, mMagnetometerReading.length);
                mMagnetometerReading = lowPass(event.values.clone(), mMagnetometerReading);
                updateUI();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);
        mTextView3 = findViewById(R.id.textView3);
        mTextView4 = findViewById(R.id.textView4);
        mTextView5 = findViewById(R.id.textView5);
        mTextView6 = findViewById(R.id.textView6);
        mTextView7 = findViewById(R.id.textView7);

        mSwitch1 = findViewById(R.id.switch1);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Magnetic field
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(magneticFieldListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateOrientationAngles();
            }
        };

        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(task, 0, 10L);
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        float valY = ((0.5f + mOrientationAngles[1]) / 2f);
        if (valY > 1) valY = 1;
        else if (valY < 0) valY = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        String valueY = df.format(valY);
        if (!valueY.equals(oldvalY)) {
            oldvalY = valueY;
            if (mSwitch1.isChecked()) {
                new SendPacket(valueY).start();
            }
        }
    }

    public void updateUI() {
        mTextView2.setText(String.format("X: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[0])));
        mTextView3.setText(String.format("Y: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[1])));
        mTextView4.setText(String.format("Z: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[2])));
        mTextView5.setText(String.format("X: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[0])));
        mTextView6.setText(String.format("Y: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[1])));
        mTextView7.setText(String.format("Z: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[2])));
        mTextView1.setText(oldvalY);
    }

    // Low pass filter method.
    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    class SendPacket extends Thread {
        DatagramSocket socket;
        String value;

        SendPacket(String value) {
            this.value = value;
            try {
                this.socket = new DatagramSocket();
            } catch (Exception e) {
                System.out.println("Package not sent: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                TextView tvIP = findViewById(R.id.ip);
                TextView tvPORT = findViewById(R.id.port);

                String ip = "" + tvIP.getText();
                String port = "" + tvPORT.getText();

                byte[] buffer = this.value.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), Integer.parseInt(port));
                System.out.println("Send to " + ip + ":" + port + ": " + this.value);

                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Package not sent: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
