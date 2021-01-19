package com.example.elmohammadymarket;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;



public class MyApp extends Application {
    public static final String CHANNEL_ID = "elmohammady";
    public static final String CHANNEL_ID_SERVICE = "elmohammady service";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        createNotificationChannelService();

    }

    private void createNotificationChannelService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_SERVICE,
                    "el mohammady",
                    NotificationManager.IMPORTANCE_NONE
            );
            channel.setDescription("El mohammady channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "el mohammady",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setDescription("El mohammady channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
