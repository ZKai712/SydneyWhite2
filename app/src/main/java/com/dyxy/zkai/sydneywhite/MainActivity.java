package com.dyxy.zkai.sydneywhite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.dyxy.zkai.sydneywhite.entity.StuMember;
import com.dyxy.zkai.sydneywhite.entity.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.stuname_edit)
    EditText stuNameEdit;
    @BindView(R.id.idcard_edit)
    EditText idCardEdit;

    private Toast toast;

    public static StuMember stuInfo = new StuMember();
    public static Task stuTask = new Task();

    //请求地址192.168.43.51     10.40.134.12
    public static final String HOST="10.40.134.4";
    public static final String UPDATEURL="http://"+HOST+":8980/oas/api/android/students/save";
    public static final String LOGINURL="http://"+HOST+":8980/oas/api/android/students/login";
    public static final String QUERYURL="http://"+HOST+":8980/oas/api/android/students/listData";
    public static final String TASKQUERY="http://"+HOST+":8980/oas/api/android/task/listData";
    public static final String TASKUPDATE="http://"+HOST+":8980/oas/api/android/task/update";

    private boolean succeed;
    private String sName;
    private String idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ok_btn)
    public void onOkBtnClicked(View v) {
        sName = stuNameEdit.getText().toString();
        idCard = idCardEdit.getText().toString();
        if("".equals(sName) || "".equals(idCard)){
            Toast.makeText(this, "学生信息不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        login(sName,idCard);

        if (!succeed){
            toast = Toast.makeText(MainActivity.this, "正在登录，请稍后\uD83D\uDE09", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void login(String name,String idcard){
        String path=LOGINURL+"?stuName="+name+"&stuIdcard="+idcard;
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(path)
                .build();
        Call call = client.newCall(request);
        //添加到消息队列里，传入Callback，当响应报文回来时执行相应的回调方法
        call.enqueue(new Callback() {
            //成功时的响应
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                Log.e("登录成功的信息返回：",result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("students")) {
                        JSONObject stu = jsonObject.getJSONObject("students");
                        String data = stu.toString();
                        Gson gson = new Gson();

                        stuInfo = gson.fromJson(data, StuMember.class);
//                        Log.e("获取的信息：",stuInfo.toString());
                    }
                    succeed = jsonObject.getBoolean("succeed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (succeed) {
                                if (toast != null){
                                    toast.cancel();
                                    toast = null;
                                }
                                Toast.makeText(MainActivity.this, "欢迎来到东营职业学院\uD83D\uDE09", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Home2Activity.class);
                                intent.putExtra("sname", sName);
                                intent.putExtra("idcard", idCard);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this, "学生信息错误或不存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //失败时的响应
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "服务器发生错误\uD83D\uDE23", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //获取任务数据
        String taskUrl=TASKQUERY+"?stuName="+name+"&stuIdcard="+idcard;
        OkHttpClient client2 = new OkHttpClient();
        Request request2 = new Request.Builder()
                .url(taskUrl)
                .build();
        Call call2 = client2.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OOOO","任务信息获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getBoolean("result")) {
                                String task = jsonObject.getString("task");
                                Gson gson = new Gson();
                                stuTask = gson.fromJson(task, Task.class);
                                Log.e("OOOOOOOOOOOO", "Task类获取的数据：" + stuTask.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });


    }


}
