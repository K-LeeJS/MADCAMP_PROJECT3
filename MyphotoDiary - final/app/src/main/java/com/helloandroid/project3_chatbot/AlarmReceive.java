package com.helloandroid.project3_chatbot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class AlarmReceive extends AppCompatActivity {   //BroadcastReceiver 가필요함

    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    final String TAG = "BOOT_START_SERVICE";



    PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    Notification.Builder builder = new Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘 설정하지 않으면 오류남
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("알림 제목") // 제목 설정
            .setContentText("알림 내용") // 내용 설정
            .setTicker("한줄 출력") // 상태바에 표시될 한줄 출력
            .setAutoCancel(true)
            .setContentIntent(intent);



        /*
//NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고

        Intent intentActivity = new Intent(context, MainActivity.class); //그메세지를 클릭했을때 불러올엑티비티를 설정함
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);//플레그부분은 옵션인데 나도 자세하게 몰르겠음
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "ticker";//여긴 알림바에 등록될 글이랑 타이틀 적는곳.
        String title = "title";
        String text = "알림";
// Create Notification Object
        Notification notification = new Notification
                (android.R.drawable.ic_input_add, ticker, System.currentTimeMillis());//알림바에 넣을 이미지 아이콘

        notification.setLatestEventInfo(context,  title, text, pendingIntent);
        */
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.notify(0, builder.build());


    }

