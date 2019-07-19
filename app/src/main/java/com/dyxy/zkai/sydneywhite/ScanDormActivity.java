package com.dyxy.zkai.sydneywhite;

import android.content.Intent;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanDormActivity extends AppCompatActivity {

    private EditText dormBuildEdit;
    private EditText dormRoomEdit;
    private EditText bedNumEdit;
    private Button okDormBtn;

    //读取后的数据
    String dormBuilding;
    String dormRoomNum;
    String dormBedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_scan);

        dormBuildEdit = findViewById(R.id.dorm_build_edit);
        dormRoomEdit = findViewById(R.id.dorm_room_edit);
        bedNumEdit = findViewById(R.id.bed_num_edit);
        okDormBtn = findViewById(R.id.ok_dorm_btn);

        Intent intent = getIntent();
        String contentDorm = intent.getStringExtra("contentDorm");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(contentDorm);
            dormBuilding = jsonObject.getString("dormBuilding");
            dormRoomNum = jsonObject.getString("dormRoomNum");
            dormBedNum = jsonObject.getString("dormBedNum");

            dormBuildEdit.setText(dormBuilding);
            dormRoomEdit.setText(dormRoomNum);
            bedNumEdit.setText(dormBedNum);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        okDormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if ((dormRoomNum==null || "".equals(dormRoomNum)) && (dormBedNum==null || "".equals(dormBedNum))){
                Toast.makeText(ScanDormActivity.this,"未识别二维码，请重新扫描",Toast.LENGTH_SHORT).show();
                return;
            }

            //保存到stuInfo中
            MainActivity.stuInfo.setStuDorm(dormBuilding+dormRoomNum+dormBedNum);

            //保存到后端
            //将stuInfo对象的信息转化为json字符串
            Gson gson = new Gson();
            String stuInfoJson = gson.toJson(MainActivity.stuInfo);
            Log.e("DORMJSON",stuInfoJson);

            //发送到后端
            saveDromInfo(stuInfoJson);
            }
        });
    }
    private void saveDromInfo(String dromInfo){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(ScanClassActivity.JSON,dromInfo);
        Request request = new Request.Builder()
                .url(MainActivity.UPDATEURL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanDormActivity.this,"成功绑定床铺",Toast.LENGTH_SHORT).show();
                        Intent intentDorm = new Intent(ScanDormActivity.this,Home2Activity.class);
                        intentDorm.putExtra("page",2);
                        intentDorm.putExtra("resultDorm",1);
                        startActivity(intentDorm);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanDormActivity.this,"绑定床铺失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}
