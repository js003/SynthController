package com.example.theja.sythcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    protected Handler myHandler;
    private int receivedMessages = 0;
    private int sendMessages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);

        mTextView1.setText("°(^.^)°");
        mTextView2.setText("Test ");

        // Message Handler
        /*myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //Bundle stuff = msg.getData();
                //mTextView2.setText(stuff.getString("messageText"));
                return true;
            }
        });*/

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public void onButtonClicked(View target) {
        String message = "Phone send message " + ++sendMessages;
        mTextView1.setText(message);
        new NewThread("/my_path", message).start();
    }

    public void sendMessage(String messageText) {
        //Bundle bundle = new Bundle();
        //bundle.putString("messageText", messageText);
        //Message msg = myHandler.obtainMessage();
        //msg.setData(bundle);
        //myHandler.sendMessage(msg);
    }

    class NewThread extends Thread {
        String path;
        String message;

        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            // Retrieve the connected devices, known as nodes
            Task<List<Node>> wearableList = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    // Send Message
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                        //sendMessage("Message " + sendMessages);
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
            String message = "Received: " + ++receivedMessages + " messages";
            mTextView2.setText(message);
        }
    }
}
