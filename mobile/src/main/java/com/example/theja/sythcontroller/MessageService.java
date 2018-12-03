package com.example.theja.sythcontroller;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println(messageEvent.getPath());
        if (messageEvent.getPath().equals("/my_path")) {
            // Receive the message
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            messageIntent.putExtra("path", messageEvent.getPath());

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else if (messageEvent.getPath().equals("/value")) {
            // Receive the message
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("value", message);
            messageIntent.putExtra("path", messageEvent.getPath());

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
