package com.helloandroid.project3_chatbot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.helloandroid.project3_chatbot.MainActivity;
import com.helloandroid.project3_chatbot.R;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //you might want to check what's inside the Intent
        if(intent.getStringExtra("myAction") != null &&
                intent.getStringExtra("myAction").equals("notify")){
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel mChannel = new NotificationChannel(
                        "Myphtodiary", "Myphtodiary", importance);

                manager.createNotificationChannel(mChannel);

            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.settings)
                    //example for large icon
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("my title")
                    .setContentText("my message")
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            i,
                            PendingIntent.FLAG_ONE_SHOT
                    );
            // example for blinking LED
            builder.setLights(0xFFb71c1c, 1000, 2000);
            //builder.setSound(yourSoundUri);
            builder.setContentIntent(pendingIntent);
            manager.notify(12345, builder.build());
        }

    }
}