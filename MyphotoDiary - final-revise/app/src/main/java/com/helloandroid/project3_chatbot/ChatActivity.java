package com.helloandroid.project3_chatbot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    EditText userInput;
    ImageView button_send;
    RecyclerView recyclerView;
    List<ResponseMessage> responseMessageList;
    MessageAdeapter messageAdeapter;
    private byte[] bitmap_data;

    private Bitmap bitmap;
    private String content = "";

    public String endword;
    public static Context context;

    private int Year,Month,Day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //endword = "끝끝끝";
        context = this;

        init();

        Intent intent = getIntent();
        endword = intent.getStringExtra("endword");
        Year = intent.getIntExtra("year",0);
        Month = intent.getIntExtra("month",0);
        Day = intent.getIntExtra("day",0);



        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    responseAction();
                }
                return true;
            }
        });
    }

    private void init() {
        userInput = (EditText) findViewById(R.id.userInput);
        button_send = (ImageView) findViewById(R.id.button_send);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdeapter = new MessageAdeapter(responseMessageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdeapter);

        buttonAction();
    }

    private void buttonAction() {
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseAction();
            }
        });
    }

    private void responseAction() {
        String currentData = userInput.getText().toString();
        String resultData = null;

        if ("안녕".equals(currentData) || "하이".equals(currentData) || "반가워".equals(currentData) || "헬로".equals(currentData)) {
            resultData = SelectWelcome();
        } else if (endword.equals(currentData)) {
            show();
            resultData = "일기 저장 완료. 오늘 하루도 수고 많았어. 내일 또 봐. 안녕!";
        } else if (currentData.equals("사용방법")) {
            resultData = "[안녕, 하이, 헬로, 반가워] 중 하나를 입력해 인사해줘. 그리고 편하게 대화하다보면 너의 일기가 완성되어 있을 거야! 마지막으로, 일기 작성을 완료하려면 '"+ endword +"'이라고 보내고 사진을 선택해줘.";
        } else {
            if (content == "") {
                content = content + currentData;
            } else {
                content = content + "\n" + currentData;
            }
            resultData = SelectReact();
        }
        ResponseMessage message = new ResponseMessage(currentData, true);
        responseMessageList.add(message);
        ResponseMessage message2 = new ResponseMessage(resultData, false);
        responseMessageList.add(message2);
        messageAdeapter.notifyDataSetChanged();

        userInput.setText("");

        if(!isVisivle()){
            recyclerView.smoothScrollToPosition(messageAdeapter.getItemCount()-1);
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void selectWeb(){
        Intent intent = new Intent(this,ImageSearchActivity.class);
        intent.putExtra("year",Year);
        intent.putExtra("month",Month);
        intent.putExtra("day",Day);
        startActivityForResult(intent, 2);
    }

    private String SelectWelcome() {
        String[] Welcomelist = {"반가워! 오늘 너의 하루는 어땠는지 들려줘.", "안녕, 오늘 하루 어땠어?",
                "보고싶었어! 오늘 하루 어떻게 보냈어?", "헬로! 오늘은 어떤 일이 있었어?", "하이! 오늘은 어땠어, 재미있는 하루였어?"};

        Random random = new Random();
        String selectWelcome = Welcomelist[random.nextInt(Welcomelist.length)];
        return selectWelcome;
    }

    private String SelectReact() {
        String[] Reactlist = {"진짜?", "아 진짜? 더 얘기해줘.", "정말?", "듣고있어, 계속 얘기해줘.", "듣고있어, 마저 얘기해줘.", "응응", "아 그래?"};

        Random random = new Random();
        String selectReact = Reactlist[random.nextInt(Reactlist.length)];
        return selectReact;
    }

    public boolean isVisivle(){
        LinearLayoutManager LinearlayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfLastVisivleItem = LinearlayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        return  (positionOfLastVisivleItem>=itemCount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        Drawable drawable = new BitmapDrawable(bitmap);
                        Intent resultIntent = new Intent();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MyPhotoDiary");
                        String photopath = path.getPath();
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


                        resultIntent.putExtra("imagepath",filepath);
                        resultIntent.putExtra("content", content);
                        setResult(RESULT_OK,resultIntent);
                        Log.e("finish","activity");
                        finish();
//                        ArrayList<CalendarDay> dates = new ArrayList<>();
//                        dates.add(new CalendarDay(Year,Month,Day));
//
//                        materialCalendarView.addDecorator(new EventDecorator(drawable, dates,MainActivity.this));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Intent resultIntent = new Intent();
                    String path = data.getStringExtra("imagepath");
                    resultIntent.putExtra("imagepath",path);
                    resultIntent.putExtra("content", content);
                    setResult(RESULT_OK,resultIntent);
                    finish();
                    break;
            }
        }
    }

    private void show()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("갤러리에서 이미지 선택");
        ListItems.add("웹에서 이미지 선택");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지를 선택하세요");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                if (selectedText == "갤러리에서 이미지 선택"){
                    selectPhoto();
                }else{
                    selectWeb();
                }
            }
        });
        builder.show();
    }

    public String getRealpath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

}
