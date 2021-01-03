package com.example.elmohammadymarket.NotificationPackage;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.elmohammadymarket.R;
import com.example.elmohammadymarket.Views.ComplaintsActivity;
import com.example.elmohammadymarket.Views.MainActivity;
import com.example.elmohammadymarket.Views.OrdersActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.elmohammadymarket.MyApp.CHANNEL_ID;
import static com.example.elmohammadymarket.MyApp.CHANNEL_ID_SERVICE;

public class OrdersNotificationsService extends Service {
    NotificationManagerCompat manager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = NotificationManagerCompat.from(this);

        Intent intent = new Intent(OrdersNotificationsService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
                .setContentTitle("سوق المحمدي")
                .setContentText("اذا اختفى هذا الاشعار اعد فتح التطبيق لتتمكن من استلام اشعارات الطلبات")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .build();

        startForeground(1, notification);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
        final NotificationManagerCompat finalManager = manager;
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Intent intent1 = new Intent(OrdersNotificationsService.this, OrdersActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(OrdersNotificationsService.this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(OrdersNotificationsService.this, CHANNEL_ID)
                        .setContentTitle("طلب جديد")
                        .setContentText("لديك طلب جديد")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .build();
                finalManager.notify(2, notification);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Complaints");
        final NotificationManagerCompat finalManager1 = manager;
        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Intent intent1 = new Intent(OrdersNotificationsService.this, ComplaintsActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(OrdersNotificationsService.this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(OrdersNotificationsService.this, CHANNEL_ID)
                        .setContentTitle("شكوي جديدة")
                        .setContentText("وصلتك شكوى جديد")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .build();
                finalManager1.notify(4, notification);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
