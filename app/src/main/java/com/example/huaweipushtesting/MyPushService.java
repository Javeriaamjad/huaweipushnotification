package com.example.huaweipushtesting;

import android.util.Log;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class MyPushService extends HmsMessageService {
    private static final String TAG = "MyPushService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "=========================================");
        Log.i(TAG, "NEW PUSH TOKEN RECEIVED!");
        Log.i(TAG, "Token: " + token);
        Log.i(TAG, "=========================================");
        
        // Send token to your server or store it locally
        // You can use this token to send push notifications to this device
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        Log.i(TAG, "=========================================");
        Log.i(TAG, "PUSH NOTIFICATION RECEIVED!");
        Log.i(TAG, "From: " + message.getFrom());
        Log.i(TAG, "Message ID: " + message.getMessageId());
        
        if (message.getNotification() != null) {
            Log.i(TAG, "Title: " + message.getNotification().getTitle());
            Log.i(TAG, "Body: " + message.getNotification().getBody());
        }
        
        if (message.getData().length() > 0) {
            Log.i(TAG, "Data: " + message.getData());
        }
        Log.i(TAG, "=========================================");
        
        // The notification will automatically appear in the notification tray
        // You can customize the notification display here if needed
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
        Log.d(TAG, "Message sent successfully. Message ID: " + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        super.onSendError(msgId, exception);
        Log.e(TAG, "Error sending message. Message ID: " + msgId + ", Error: " + exception.getMessage());
    }
}

