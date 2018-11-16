package com.example.theja.sythcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity implements WearableNavigationDrawerView.OnItemSelectedListener {

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
    private WearableNavigationDrawerView mNavigationDrawer;
    private List<Menu> menuList;
    private ConstraintLayout mMessageLayout;
    private ConstraintLayout mVectorLayout;
    private ConstraintLayout mGyroscopeLayout;
    private ConstraintLayout mMatrixLayout;

    private float x = 0;
    private float y = 0;
    private float z = 0;

    private float x_init = 0;
    private float y_init = 0;
    private float z_init = 0;
    private TextView mX;
    private TextView mY;
    private TextView mZ;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private TextView mTextView13;
    private TextView mTextView14;
    private TextView mTextView15;
    private TextView mTextView16;
    private TextView mTextView17;
    private TextView mTextView18;


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

        // Test Accelerometer and MagneticField Sensor
        mTextView13 = findViewById(R.id.textView13);
        mTextView14 = findViewById(R.id.textView14);
        mTextView15 = findViewById(R.id.textView15);
        mTextView16 = findViewById(R.id.textView16);
        mTextView17 = findViewById(R.id.textView17);
        mTextView18 = findViewById(R.id.textView18);
        mMatrixLayout = findViewById(R.id.matrixLayout);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.arraycopy(event.values, 0, mAccelerometerReading,0, mAccelerometerReading.length);
                updateOrientationAngles();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorEventListener magneticFieldListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.arraycopy(event.values, 0, mMagnetometerReading,0, mMagnetometerReading.length);
                updateOrientationAngles();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(magneticFieldListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        // Gyroscope
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
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
        sensorManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);

        // Rotation vector
        mX = findViewById(R.id.init_x);
        mY = findViewById(R.id.init_y);
        mZ = findViewById(R.id.init_z);
        Sensor rotvec = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
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

                x = orientations[0];
                y = orientations[1];
                z = orientations[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(rotvecListener, rotvec, SensorManager.SENSOR_DELAY_NORMAL);

        // Enables Always-on
        setAmbientEnabled();

        // Send Message Stuff
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

        // Top navigation drawer
        menuList = new ArrayList<Menu>();

        mVectorLayout = findViewById(R.id.vectorLayout);
        mMessageLayout = findViewById(R.id.messageLayout);
        mGyroscopeLayout = findViewById(R.id.gyroLayout);

        menuList.add(new Menu("Acceleration / magnetic field", android.R.drawable.ic_menu_mapmode));
        menuList.add(new Menu("Rotation vector", android.R.drawable.ic_menu_rotate));
        menuList.add(new Menu("Messages", android.R.drawable.ic_dialog_email));
        menuList.add(new Menu("Gyroscope", android.R.drawable.ic_menu_compass));

        mNavigationDrawer = findViewById(R.id.top_navigation_drawer);
        mNavigationDrawer.setAdapter(new NavigationAdapter(this));
        mNavigationDrawer.getController().peekDrawer();
        mNavigationDrawer.addOnItemSelectedListener(this);
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.

        mTextView13.setText(String.format("X: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[0])));
        mTextView14.setText(String.format("Y: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[1])));
        mTextView15.setText(String.format("Z: %s", String.format(Locale.getDefault(), "%.2f", mRotationMatrix[2])));
        mTextView16.setText(String.format("X: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[0])));
        mTextView17.setText(String.format("Y: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[1])));
        mTextView18.setText(String.format("Z: %s", String.format(Locale.getDefault(), "%.2f", mOrientationAngles[2])));

    }

     public void onButtonClicked(View target) {
        String onClickMessage = "Sended " + ++sendMessages + " messages";
        t3.setText(onClickMessage);
        String datapath = "/my_path";
        new SendMessage(datapath, onClickMessage).start();
     }

     public void onInitButtonClicked(View target) {
        x_init = x;
        y_init = y;
        z_init = z;

         mX.setText(String.format("%s°", String.format(Locale.getDefault(), "%.2f", x_init)));
         mY.setText(String.format("%s°", String.format(Locale.getDefault(), "%.2f", y_init)));
         mZ.setText(String.format("%s°", String.format(Locale.getDefault(), "%.2f", z_init)));
     }

    @Override
    public void onItemSelected(int i) {
        if (i == 0) {
            mMatrixLayout.setVisibility(View.VISIBLE);
            mVectorLayout.setVisibility(View.INVISIBLE);
            mMessageLayout.setVisibility(View.INVISIBLE);
            mGyroscopeLayout.setVisibility(View.INVISIBLE);

        } else if (i == 1) {
            mMatrixLayout.setVisibility(View.INVISIBLE);
            mVectorLayout.setVisibility(View.VISIBLE);
            mMessageLayout.setVisibility(View.INVISIBLE);
            mGyroscopeLayout.setVisibility(View.INVISIBLE);
        } else if (i == 2) {
            mMatrixLayout.setVisibility(View.INVISIBLE);
            mVectorLayout.setVisibility(View.INVISIBLE);
            mMessageLayout.setVisibility(View.VISIBLE);
            mGyroscopeLayout.setVisibility(View.INVISIBLE);
        } else if (i == 3) {
            mMatrixLayout.setVisibility(View.INVISIBLE);
            mVectorLayout.setVisibility(View.INVISIBLE);
            mMessageLayout.setVisibility(View.INVISIBLE);
            mGyroscopeLayout.setVisibility(View.VISIBLE);
        }
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

    private final class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {

        private final Context mContext;

        public NavigationAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public String getItemText(int index) {
            return menuList.get(index).getText();
        }

        @Override
        public Drawable getItemDrawable(int index) {
            return mContext.getDrawable(menuList.get(index).getIcon());
        }
    }
}