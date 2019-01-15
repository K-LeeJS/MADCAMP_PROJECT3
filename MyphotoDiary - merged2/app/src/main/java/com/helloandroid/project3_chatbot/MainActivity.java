package com.helloandroid.project3_chatbot;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.helloandroid.project3_chatbot.decorators.EventDecorator;
import com.helloandroid.project3_chatbot.decorators.OneDayDecorator;
import com.helloandroid.project3_chatbot.decorators.SaturdayDecorator;
import com.helloandroid.project3_chatbot.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String time,menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    private MaterialCalendarView materialCalendarView;
    private String username;
    private android.support.v7.widget.Toolbar myToolBar;
    private String selected_day;
    private int Year,Month,Day;
    public SharedPreferences prefs;
    private SQLiteDatabase database;
    private String databasename = "MyPhotoDiary";
    private String tablename = "photoDiary";

    String photopath;
    private String endword;
    private String date, path, contents;

    private TextView textView;
    private TextView textView2;
    private ImageView imageView;
    private TextView button, button2;
    private int hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestForPermission();
        //setTitle(username+"'s Photo Diary");

        openDatabase(databasename);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (TextView) findViewById(R.id.button);
        button2 = (TextView) findViewById(R.id.button2);



        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MyPhotoDiary");
        photopath = path.getPath();
        Log.e("file",photopath);

        if(path.mkdirs()){
            Log.e("FILE", "Directory not created");
        }else{
            //Toast.makeText(this, "폴더 생성 SUCCESS", Toast.LENGTH_SHORT).show();
        }

        //new AlarmHATT(getApplicationContext()).Alarm();

        //alarm_on();





        prefs = getSharedPreferences("data",Activity.MODE_PRIVATE);
        username = prefs.getString("username",null);
        if (username != null){
            setTitle(username+"'s Photo Diary");
        }
        endword = prefs.getString("endword","끝끝끝");
        hour = prefs.getInt("hour",20);
        minute = prefs.getInt("minute",0);

        alarm_on();

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);


        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(2040, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Year = date.getYear();
                Month = date.getMonth();
                Day = date.getDay();
                String date2 = String.valueOf(Year) + "/" + String.valueOf(Month) + "/" + String.valueOf(Day);

                materialCalendarView.clearSelection();


                viewOrinsert(date2);
                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                */

                /*
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("endword",endword);
                startActivityForResult(intent,1);
                */

                //Toast.makeText(getApplicationContext(), shot_Day , Toast.LENGTH_SHORT).show();
            }
        });
        selectData(tablename);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            switch (requestCode){
                case 1:
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        contents = data.getStringExtra("content");
                        byte[] bytedata = data.getByteArrayExtra("image");
                        ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
                        Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
                        Drawable drawable = new BitmapDrawable(bitmap);
                        ArrayList<CalendarDay> dates = new ArrayList<>();
                        dates.add(new CalendarDay(Year,Month,Day));
                        materialCalendarView.addDecorator(new EventDecorator(drawable, dates,MainActivity.this));

                        String filepath = photopath+File.separator+"image"+Year+Month+Day+".jpg";
                        File fileCacheItem = new File(filepath);
                        OutputStream out = null;
                        try
                        {
                            fileCacheItem.createNewFile();
                            out = new FileOutputStream(fileCacheItem);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        }
                        catch (Exception e)
                        { e.printStackTrace(); }
                        finally
                        { try { out.close(); }
                            catch (IOException e) { e.printStackTrace(); }
                        }
                        path = filepath;

                        date = String.valueOf(Year) + "/" + String.valueOf(Month) + "/" + String.valueOf(Day);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        insertData(date, path, contents);

        String sql = "SELECT * FROM photoDiary WHERE date = '"+date+"'";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.getCount()!=0) {
            viewOrinsert(date);
        }
    }



    private void getUserName(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("당신은 누구인가요?");
        //builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        username = edittext.getText().toString();
                        setTitle(username+"'s Photo Diary");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username",username);
                        editor.commit();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void getEndWord(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료 단어를 입력해주세요");
        //builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        endword = edittext.getText().toString();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("endword",endword);
                        editor.commit();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void getTime(){
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int aaminute) {
// 설정버튼 눌렀을 때
                hour = hourOfDay;
                minute = aaminute;

                Intent intent = new Intent();
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                manager.cancel(sender);
                alarm_on();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("hour",hour);
                editor.putInt("minute",minute);
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this,listener,12,0,false);
        dialog.show();
    }

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;

    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu, menu);
        return true;
    }




    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.set_user_name:
                // User chose the "Settings" item, show the app settings UI...
                getUserName();
                return true;

            case R.id.set_end_word:
                getEndWord();
                return true;
            case R.id.set_time:
                getTime();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewOrinsert(final String string){
        //Log.d("생성됨", string);
        //String sql = "SELECT path, contents FROM photoDiary WHERE date = "+string;
        String sql = "SELECT * FROM photoDiary WHERE date = '"+string+"'";
        //String sql = "SELECT * FROM photoDiary WHERE date=" + string + ";";
        Cursor cursor = database.rawQuery(sql, null);
        Log.d("조회 생성됨", String.valueOf(cursor.getCount()));

        if (cursor.getCount()==0){
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("endword",endword);
            startActivityForResult(intent,1);
        } else {
            for (int i=0; i<cursor.getCount(); i++) {
                cursor.moveToNext();

                final String date = cursor.getString(1);
                final String path = cursor.getString(2);
                final String contents = cursor.getString(3);

                //textView.setText(string);

                Bitmap bitmap = BitmapFactory.decodeFile(path); //사이즈 조정 추가?
                final Drawable drawable = new BitmapDrawable(bitmap);
                imageView.setImageDrawable(drawable);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PopActivity.class);
                        intent.putExtra("date", date);
                        intent.putExtra("path", path);
                        intent.putExtra("contents", contents);
                        startActivity(intent);
                    }
                });

                String datearray[] = date.split("/");
                int year = Integer.parseInt(datearray[0]);
                int month = Integer.parseInt(datearray[1]);
                int day = Integer.parseInt(datearray[2]);
                String dateNew = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day);

                textView.setText(dateNew);
                textView2.setText(contents);

                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("endword",endword);
                        startActivityForResult(intent,1);
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() { //삭제 후 해당 날짜 다시 들어갔다가 나오면 삭제됐던 데이터가 복구되는 문제점
                    @Override
                    public void onClick(View v) {
                        String sql = "DELETE FROM photoDiary WHERE date = '"+string+"'";
                        database.execSQL(sql);
                        Log.d("삭제", "생성됨.");

                        materialCalendarView.removeDecorators();
                        selectData(tablename);

                        textView.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        button2.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    private void selectData(String tableName) {
        if (database != null){
            String sql = "select date, path, contents from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("뿌리기 생성됨", String.valueOf(cursor.getCount()));

            for (int i=0; i<cursor.getCount(); i++){
                cursor.moveToNext();
                String date = cursor.getString(0);
                String path = cursor.getString(1);
                String contents = cursor.getString(2);

                //date 연월일로 분해
                String datearray[] = date.split("/");
                int year = Integer.parseInt(datearray[0]);
                int month = Integer.parseInt(datearray[1]);
                int day = Integer.parseInt(datearray[2]);

                //사진 경로 비트맵으로
                Bitmap bitmap = BitmapFactory.decodeFile(path); //사이즈 조정 추가?
                Drawable drawable = new BitmapDrawable(bitmap);
                ArrayList<CalendarDay> dates = new ArrayList<>();
                dates.add(new CalendarDay(year,month,day));
                materialCalendarView.addDecorator(new EventDecorator(drawable, dates,MainActivity.this));
            }
            cursor.close();
        }
    }

    private void insertData(String date, String path, String contents) {

        if (database != null){
            if (path != null && contents != null) {
                String sql = "INSERT OR REPLACE INTO " + tablename + " (date, path, contents) Values (?, ?, ?);";
                Object[] params = {date, path, contents};
                database.execSQL(sql, params);

                Log.d("데이터 추가(생성됨)", date + "/" + path + "/" + contents);
            } else {}
        } else {

        }
    }

    private void createTable(String tableName) {

        if (database != null) {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (_id integer PRIMARY KEY autoincrement, date text, path text, contents text );");
            Log.d("테이블", "생성됨.");
        } else {}
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null){
            Log.d("데이터베이스", "생성됨.");
            createTable(tablename);
        }
    }

    public void alarm_on(){
        // 알람 등록하기
        Log.i("alarm", "setAlarm");
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Notification.class);   //AlarmReceive.class이클레스는 따로 만들꺼임 알람이 발동될때 동작하는 클레이스임

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour,minute);//시간을 10시 01분으로 일단 set했음
        calendar.set(Calendar.SECOND, 0);

        //알람 예약
        //am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);//이건 한번 알람
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, sender);//이건 여러번 알람 24*60*60*1000 이건 하루에한번 계속 알람한다는 뜻.
        Toast.makeText(this,"시간설정:"+ Integer.toString(calendar.get(calendar.HOUR_OF_DAY))+":"+Integer.toString(calendar.get(calendar.MINUTE)),Toast.LENGTH_LONG).show();
    }




}
