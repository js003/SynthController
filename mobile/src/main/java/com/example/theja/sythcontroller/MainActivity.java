package com.example.theja.sythcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;


public class MainActivity extends AppCompatActivity implements MessageClient.OnMessageReceivedListener {

    private TextView mTextView1;
    private TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);

        mTextView1.setText("°(^.^)°");
        mTextView2.setText("Test ");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        mTextView1.setText(messageEvent.getPath());
        mTextView2.setText(messageEvent.toString());
        //if (messageEvent.getPath()) {
        //    Intent startIntent = new Intent(this, MainActivity.class);
        //    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    startIntent.putExtra("VOICE_DATA", messageEvent.getData());
        //    startActivity(this, startIntent);
        //}
    }
}
