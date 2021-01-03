package com.example.elmohammadymarket.NotificationPackage;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.example.elmohammadymarket.R;

import static com.example.elmohammadymarket.MyApp.CHANNEL_ID;

public class GetNotification {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification getNotification(Context context, String title, String message, Intent intent) {
        NotificationManagerCompat manager = null;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .build();
        manager = NotificationManagerCompat.from(context);
        manager.notify(0, notification);
        return notification;
    }
}
