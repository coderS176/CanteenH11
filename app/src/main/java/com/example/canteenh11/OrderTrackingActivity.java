package com.example.canteenh11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderTrackingActivity extends AppCompatActivity {

    private TextView textViewTimer, textViewEstimatedFinishTime, textViewOrderStatus, textViewText;
    private Handler handler;
    private Runnable timerRunnable;
    private long targetTimeMillis;
    private FirebaseFirestore db;
    private ArrayList<String> itemNames;
    private static final String TAG = "OrderTrackingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);
        String EstimatedTime = getIntent().getStringExtra("EstimatedTime");
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewEstimatedFinishTime = findViewById(R.id.textViewEstimatedFinishTime);
        textViewText = findViewById(R.id.textViewText);
        textViewOrderStatus = findViewById(R.id.textViewOrderStatus);
        String time = EstimatedTime.substring(EstimatedTime.indexOf(' ') + 1);
        textViewEstimatedFinishTime.setText("Estimated Finish Time:" + time);
        handler = new Handler();
        targetTimeMillis = stringToMilliseconds(EstimatedTime);
        long notificationTimeMillis = targetTimeMillis - System.currentTimeMillis();
        if (notificationTimeMillis > 0) {
            scheduleNotification(this, notificationTimeMillis);
        }
        startTimer();
    }


    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimer();
                if (System.currentTimeMillis() >= targetTimeMillis) {
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(timerRunnable);
    }

    private void updateTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        long remainingMillis = targetTimeMillis - currentTimeMillis;
        if (remainingMillis <= 0) {
            textViewTimer.setText("");
            textViewOrderStatus.setText("Order Completed");
            textViewEstimatedFinishTime.setText("Go and Grab Your Food From Mess");
            textViewText.setText("");
//            NotificationHelper.sendNotification(this, "FoodReady", "PickYourFoodFromMess");
            return;
        }
        long minutes = (remainingMillis / 1000) / 60;
        long seconds = (remainingMillis / 1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeString);

    }

    public static long stringToMilliseconds(String timeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(timeStr);
            return date.getTime(); // Returns milliseconds since epoch
        } catch (ParseException e) {
            // Error handling for invalid input
            e.printStackTrace();
            return -1; // Or throw an exception
        }
    }

    public void scheduleNotification(Context context, long notificationTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("NOTIFICATION_ACTION"); // Custom action for the broadcast receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarm
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTimeMillis, pendingIntent);
        }
    }

}
