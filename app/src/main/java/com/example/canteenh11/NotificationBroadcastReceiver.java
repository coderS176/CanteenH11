package com.example.canteenh11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("NOTIFICATION_ACTION".equals(intent.getAction())) {
            // Handle the notification logic here
            NotificationHelper.sendNotification(context, "Food Ready", "Pick Your Food From Mess","#123");
        }
    }
}
