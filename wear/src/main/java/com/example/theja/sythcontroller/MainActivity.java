package com.example.theja.sythcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private TextView t3;
    private TextView t4;
    private int sendMessages = 0;
    private int receivedMessages = 0;

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

        // Send Message Stuff
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);
    }

     public void onButtonClicked(View target) {
        String onClickMessage = "Wearable send message " + ++sendMessages;
        t3.setText(onClickMessage);
        String datapath = "/my_path";
        new SendMessage(datapath, onClickMessage).start();
     }

     class SendMessage extends Thread {
        String path;
        String message;

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                    } catch (ExecutionException e) {

                    } catch (InterruptedException e) {

                    }
                }
            } catch (ExecutionException e) {

            } catch (InterruptedException e) {

            }
        }
     }

     public class Receiver extends BroadcastReceiver {
        @Override
         public void onReceive(Context context, Intent intent) {
            String onMessageReceived = "Received " + ++receivedMessages + " messages";
            t4.setText(onMessageReceived);
        }
     }
}