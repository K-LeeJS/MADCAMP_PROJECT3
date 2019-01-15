package com.helloandroid.project3_chatbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class PopActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();

        final String date = intent.getExtras().getString("date");
        final String path = intent.getExtras().getString("path");
        final String contents = intent.getExtras().getString("contents");

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Drawable drawable = new BitmapDrawable(bitmap);

        String datearray[] = date.split("/");
        int year = Integer.parseInt(datearray[0]);
        int month = Integer.parseInt(datearray[1]);
        int day = Integer.parseInt(datearray[2]);
        String dateNew = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day);

        imageView.setImageDrawable(drawable);
        textView.setText(contents);
        textView2.setText(dateNew);

    }
}
