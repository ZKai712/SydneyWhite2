package com.dyxy.zkai.sydneywhite;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dyxy.zkai.sydneywhite.view.MineView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanClassActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String TAG = "ScanClassActivity";

    private EditText classFacultyEdit;
    private EditText classNameEdit;
    private EditText stuTotalEdit;
    private EditText teacherEdit;
    private Button okClassBtn;

    private final String QUERYCLASS = "http://"+MainActivity.HOST+":8980/oas/api/android/class/classList";

    String faculty;
    String className;
    String stuTotal;
    String teacher;
    String teacherPhone;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String classData = (String) msg.obj;
            try {
                JSONObject jsonObject = new JSONObject(classData);
                if (jsonObject.getBoolean("result")) {
                    JSONObject clazzJson = jsonObject.getJSONObject("clazz");
                    faculty = clazzJson.getString("faculty");
                    className = clazzJson.getString("sclass");
                    stuTotal = clazzJson.getString("classSize");
                    teacher = clazzJson.getString("classTeacher");
                    teacherPhone = clazzJson.getString("teacherPhone");

                    classFacultyEdit.setText(faculty);
                    classNameEdit.setText(className);
                    stuTotalEdit.setText(stuTotal);
                    teacherEdit.setText(teacher);
                }else {
                    Toast.makeText(ScanClassActivity.this,"获取班级信息失败",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_scan);

        classFacultyEdit = findViewById(R.id.class_faculty_edit);
        classNameEdit = findViewById(R.id.class_name_edit);
        stuTotalEdit = findViewById(R.id.total_edit);
        teacherEdit = findViewById(R.id.cteacher_edit);
        okClassBtn = findViewById(R.id.ok_class_btn);

        Intent intent = getIntent();
        final String content = intent.getStringExtra("content");
        Log.e("deaf",content);
        try {
            JSONObject jsonObject = new JSONObject(content);
            String classid = jsonObject.getString("id");

            //向网络发送请求查询此班级
            String url = QUERYCLASS+"?classid"+classid;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(QUERYCLASS)
                    .get().build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG,"网络失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String classData = response.body().string();
//                    Log.e(TAG,"班级信息"+classData);

                    Message msg = handler.obtainMessage();
                    msg.obj = classData;
                    handler.sendMessage(msg);
                }
            });




        } catch (JSONException e) {
            e.printStackTrace();
        }

        okClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((faculty==null || "".equals(faculty)) && (className==null || "".equals(className))){
                    Toast.makeText(ScanClassActivity.this,"未识别二维码，请重新扫描",Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存到stuInfo中
                MainActivity.stuInfo.setFaculty(faculty);
                MainActivity.stuInfo.setSclass(className);
                MainActivity.stuInfo.setClassSize(Integer.parseInt(stuTotal));
                MainActivity.stuInfo.setClassTeacher(teacher);
                MainActivity.stuInfo.setTeacherPhone(teacherPhone);
                //将stuInfo对象的信息转化为json字符串
                Gson gson = new Gson();
                String stuInfoJson = gson.toJson(MainActivity.stuInfo);
//                Log.e("CLASSJSON",stuInfoJson);
                //保存到jeesite
                saveClassInfo(stuInfoJson);

            }
        });
    }

    private void saveClassInfo(String infoJson){
        //创建okhttp客户端
        OkHttpClient client = new OkHttpClient();
        //添加json请求
        RequestBody body = RequestBody.create(JSON,infoJson);

        Request request = new Request.Builder()
                .url(MainActivity.UPDATEURL)
                .post(body)    //将body加进来
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanClassActivity.this,"成功加入班级",Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(ScanClassActivity.this,Home2Activity.class);
                        intent2.putExtra("resultClass",1);
                        intent2.putExtra("page",1);
                        startActivity(intent2);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanClassActivity.this,"加入班级失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
