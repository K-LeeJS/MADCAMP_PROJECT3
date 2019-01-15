package com.helloandroid.project3_chatbot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class Notification extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        createNotification();

    }


    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        builder.setSmallIcon(R.drawable.pushdiary);
        builder.setContentTitle("일기쓸 시간이에요!");
        //builder.setContentText("알람 세부 텍스트");

        builder.setColor(0xFF54C1CC);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        builder.setOngoing(false);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        i,
                        PendingIntent.FLAG_ONE_SHOT
                );

        builder.setContentIntent(pendingIntent);


        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
}